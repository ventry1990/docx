package org.ventry.docx;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * file: org.ventry.docx.PictureReader
 * author: ventry
 * create: 18/5/19 15:33
 * description:
 */

abstract class PictureReader {
    static final String XMLNS_A
            = "declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main';";
    static final String XMLNS_WP
            = "declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing';";
    static final String XMLNS_V
            = "declare namespace v='urn:schemas-microsoft-com:vml';";

    private final PictureProcessor processor;

    PictureReader(PictureProcessor processor) {
        this.processor = processor;
    }

    abstract boolean match(XmlObject object);

    CharSequence read(XWPFDocument document, XmlObject object) throws IOException {
        PictureData picture = readPictureStream(document, object);

        StringBuilder img = new StringBuilder("<img ");
        img.append("src='").append(process(picture)).append("' ");
        if (picture.isShaped()) {
            img.append("width='")
                    .append(LengthUnit.POINTS.toPixels(picture.getWidth()))
                    .append("' ")
                    .append("height='")
                    .append(LengthUnit.POINTS.toPixels(picture.getHeight()))
                    .append("' ");
        }

        if (picture.isAnchored()) {
            img.append(" style='position:absolute;margin-top:")
                    .append(LengthUnit.POINTS.toPixels(picture.getTop()))
                    .append("px;")
                    .append("margin-left:")
                    .append(LengthUnit.POINTS.toPixels(picture.getLeft()))
                    .append("px;' ");
        }

        img.append("/>");
        return img;
    }

    protected abstract PictureData readPictureStream(XWPFDocument document, XmlObject drawing);

    private String process(PictureData picture) throws IOException {
        if (picture.getData().length == 0)
            return "unknown";

        try {
            if (picture.isWmf()) {
                String newName = picture.getName() + ".png";
                return processor.process(newName, WmfConverter.convert(picture.getStream()));
            } else if (picture.isEmf()) {
                String newName = picture.getName() + ".png";
                return processor.process(newName, EmfConverter.convert(picture.getStream()));
            } else {
                try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
                    bytes.write(picture.getData());
                    return processor.process(picture.getName(), bytes);
                }
            }
        } finally {
            if (picture.getStream() != null) {
                picture.getStream().close();
            }
        }
    }
}
