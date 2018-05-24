package org.ventry.docx;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.ventry.WordReader;
import org.ventry.docx.picture.DrawingReader;
import org.ventry.docx.picture.ImageObjectReader;
import org.ventry.docx.picture.PictureProcessor;
import org.ventry.docx.picture.PictureReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * file: org.ventry.docx.WordReaderImpl
 * author: ventry
 * create: 18/5/19 15:24
 * description:
 */

public class WordReaderImpl implements WordReader {
    private final XWPFDocument document;
    private final List<PictureReader> pictureReaders;
    private final Map<Class<? extends IBodyElement>, Function<IBodyElement, CharSequence>> bodyFunctions;

    public WordReaderImpl(InputStream input, PictureProcessor pictureProcessor) throws IOException {
        this.bodyFunctions = new HashMap<>();
        this.bodyFunctions.put(XWPFTable.class, el -> ((XWPFTable) el).getText());
        this.bodyFunctions.put(XWPFParagraph.class, el -> readParagraph((XWPFParagraph) el));
        this.bodyFunctions.put(XWPFSDT.class, el -> ((XWPFSDT) el).getContent().getText());

        this.pictureReaders = Arrays.asList(
                new DrawingReader(pictureProcessor),
                new ImageObjectReader(pictureProcessor));

        this.document = new XWPFDocument(input);
    }

    public String read() throws IOException {
        List<IBodyElement> elements = document.getBodyElements();
        StringBuilder content = new StringBuilder();
        for (IBodyElement element : elements) {
            Function<IBodyElement, CharSequence> function = bodyFunctions.get(element.getClass());
            if (function != null) {
                content.append("<p>").append(function.apply(element))
                        .append("</p>").append('\n');
            }
        }

        return content.toString();
    }

    private StringBuilder readParagraph(XWPFParagraph paragraph) {
        StringBuilder content = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(run.getTextPosition());
            if (text != null) {
                content.append(text);
                continue;
            }

            tryAppendPicture(content, run);
        }

        return content;
    }

    private void tryAppendPicture(StringBuilder content, XWPFRun run) {
        XmlCursor cursor = run.getCTR().newCursor();
        cursor.selectPath("./*");
        while (cursor.toNextSelection()) {
            XmlObject xmlObject = cursor.getObject();
            pictureReaders.forEach(reader -> {
                if (reader.match(xmlObject)) {
                    try {
                        content.append(reader.read(document, xmlObject));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        cursor.dispose();
    }
}