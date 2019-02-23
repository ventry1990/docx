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
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

/**
 * file: org.ventry.docx.DocxReader
 * author: ventry
 * create: 18/5/19 15:24
 * description:
 */

public class DocxReader implements WordReader {
    private static final String NAMESPACES = XmlNamespace.concat(XmlNamespace.W, XmlNamespace.M);
    private static final String ELEMENTS = ".//w:t | .//w:drawing | .//w:object | .//m:oMath | .//w:pict";
    private static final String CURSOR_SELECT_PATH = NAMESPACES + ELEMENTS;

    private final Map<BodyElementType, Consumer<IBodyElement>> bodyFunctions;
    private final ContentBuilder contentBuilder;
    private final XWPFDocument document;
    private final List<ContentReader> readers;

    DocxReader(InputStream input, PictureProcessor pictureProcessor, Executor executor) throws IOException {
        contentBuilder = new ContentBuilder(executor);

        bodyFunctions = new HashMap<>();
        bodyFunctions.put(BodyElementType.TABLE, el -> readTable((XWPFTable) el));
        bodyFunctions.put(BodyElementType.PARAGRAPH, el -> readParagraph((XWPFParagraph) el));
        bodyFunctions.put(BodyElementType.CONTENTCONTROL, el -> contentBuilder.append(((XWPFSDT) el).getContent().getText()));

        document = new XWPFDocument(input);
        readers = Arrays.asList(
                new TextReader(),
                new FormulaReader(),
                new DrawingReader(this.document, pictureProcessor),
                new ImageObjectReader(this.document, pictureProcessor));
    }

    public String read() throws IOException {
        List<IBodyElement> elements = document.getBodyElements();
        for (IBodyElement element : elements) {
            Consumer<IBodyElement> consumer = bodyFunctions.get(element.getElementType());
            if (consumer != null) {
                contentBuilder.append("<p>");
                consumer.accept(element);
                contentBuilder.append("</p>").append("\n");
            }
        }

        return contentBuilder.build();
    }

    private void readTable(XWPFTable table) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    readParagraph(paragraph);
                }
                contentBuilder.append("\t");
            }
            contentBuilder.append("\n");
        }
    }

    private void readParagraph(XWPFParagraph paragraph) {
        XmlCursor cursor = paragraph.getCTP().newCursor();
        cursor.selectPath(CURSOR_SELECT_PATH);
        while (cursor.toNextSelection()) {
            tryRead(cursor.getObject());
        }
        cursor.dispose();
    }

    private void tryRead(XmlObject object) {
        contentBuilder.append(() -> {
            for (ContentReader reader : readers) {
                if (reader.match(object)) {
                    return reader.read(object);
                }
            }
            return "";
        });
    }

    public static class Builder {
        private Executor executor;
        private InputStream documentStream;
        private PictureProcessor pictureProcessor;

        public DocxReader build() throws IOException {
            Objects.requireNonNull(documentStream);
            Objects.requireNonNull(pictureProcessor);
            Executor asyncExecutor = executor == null ? ForkJoinPool.commonPool() : executor;
            return new DocxReader(documentStream, pictureProcessor, asyncExecutor);
        }

        public Builder setExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public Builder setDocumentStream(InputStream documentStream) {
            this.documentStream = documentStream;
            return this;
        }

        public Builder setPictureProcessor(PictureProcessor pictureProcessor) {
            this.pictureProcessor = pictureProcessor;
            return this;
        }
    }
}