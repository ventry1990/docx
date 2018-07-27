package org.ventry.docx;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.ventry.WordReader;
import org.ventry.docx.formula.FormulaReader;
import org.ventry.docx.picture.DrawingReader;
import org.ventry.docx.picture.ImageObjectReader;
import org.ventry.docx.picture.PictureProcessor;
import org.ventry.docx.text.TextReader;

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
    private final List<ContentReader> readers;
    private final Map<BodyElementType, Function<IBodyElement, CharSequence>> bodyFunctions;

    public WordReaderImpl(InputStream input, PictureProcessor pictureProcessor) throws IOException {
        this.bodyFunctions = new HashMap<>();
        this.bodyFunctions.put(BodyElementType.TABLE, el -> readTable((XWPFTable) el));
        this.bodyFunctions.put(BodyElementType.PARAGRAPH, el -> readParagraph((XWPFParagraph) el));
        this.bodyFunctions.put(BodyElementType.CONTENTCONTROL, el -> ((XWPFSDT) el).getContent().getText());

        this.document = new XWPFDocument(input);
        this.readers = Arrays.asList(
                new TextReader(),
                new FormulaReader(),
                new DrawingReader(this.document, pictureProcessor),
                new ImageObjectReader(this.document, pictureProcessor));
    }

    public String read() throws IOException {
        List<IBodyElement> elements = document.getBodyElements();
        StringBuilder content = new StringBuilder();
        for (IBodyElement element : elements) {
            Function<IBodyElement, CharSequence> function = bodyFunctions.get(element.getElementType());
            if (function != null) {
                content.append("<p>").append(function.apply(element))
                        .append("</p>").append('\n');
            }
        }

        return content.toString();
    }

    private StringBuilder readTable(XWPFTable table) {
        StringBuilder content = new StringBuilder();
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    content.append(readParagraph(paragraph));
                }
                content.append("\t");
            }
            content.append('\n');
        }
        return content;
    }

    private StringBuilder readParagraph(XWPFParagraph paragraph) {
        StringBuilder content = new StringBuilder();
        XmlCursor cursor = paragraph.getCTP().newCursor();
        String namespaces = XmlNamespace.concat(XmlNamespace.W, XmlNamespace.M);
        String path = ".//w:t | .//w:drawing | .//w:object | .//m:oMath";
        cursor.selectPath(namespaces + path);
        while (cursor.toNextSelection()) {
            content.append(tryRead(cursor.getObject()));
        }
        cursor.dispose();
        return content;
    }

    private CharSequence tryRead(XmlObject object) {
        for (ContentReader reader : readers) {
            if (reader.match(object)) {
                return reader.read(object);
            }
        }
        return "";
    }
}