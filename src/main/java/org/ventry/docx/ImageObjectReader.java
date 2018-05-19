package org.ventry.docx;

import com.microsoft.schemas.vml.CTImageData;
import com.microsoft.schemas.vml.CTShape;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * file: org.ventry.docx.ImageObjectReader
 * author: ventry
 * create: 18/5/19 15:39
 * description:
 */

class ImageObjectReader extends PictureReader {
    private static final Pattern WIDTH_PATTERN = Pattern.compile("(?<=width:)\\d+(?:\\.\\d+)?(?=pt)");
    private static final Pattern HEIGHT_PATTERN = Pattern.compile("(?<=height:)\\d+(?:\\.\\d+)?(?=pt)");

    ImageObjectReader(PictureProcessor processor) {
        super(processor);
    }

    @Override
    boolean match(XmlObject object) {
        return object instanceof CTObject;
    }

    @Override
    protected PictureData readPictureStream(XWPFDocument document, XmlObject object) {
        CTObject image = (CTObject) object;
        PictureData pictureData = new PictureData();

        XmlCursor cursor = image.newCursor();
        cursor.selectPath(XMLNS_V + ".//v:imagedata");
        if (cursor.toNextSelection()) {
            CTImageData imageData = (CTImageData) cursor.getObject();
            String relationId = imageData.getId2();
            XWPFPictureData picture = (XWPFPictureData) document.getRelationById(relationId);
            pictureData = new PictureData(picture.getFileName(), picture.getData());
            sizeTo(image, pictureData);
        }
        cursor.dispose();

        return pictureData;
    }

    private void sizeTo(CTObject image, PictureData picture) {
        XmlCursor cursor = image.newCursor();
        cursor.selectPath(XMLNS_V + "./v:shape");
        if (cursor.toNextSelection()) {
            String style = ((CTShape) cursor.getObject()).getStyle();
            Matcher widthMatcher = WIDTH_PATTERN.matcher(style);
            double width = 0D;
            if (widthMatcher.find()) {
                width = Double.valueOf(widthMatcher.group());
            }

            Matcher heightMatcher = HEIGHT_PATTERN.matcher(style);
            double height = 0D;
            if (heightMatcher.find()) {
                height = Double.valueOf(heightMatcher.group());
            }

            picture.sizeTo(width, height);
        }
        cursor.dispose();
    }
}
