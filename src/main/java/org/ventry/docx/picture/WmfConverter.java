package org.ventry.docx.picture;

import net.arnx.wmf2svg.gdi.svg.SvgGdi;
import net.arnx.wmf2svg.gdi.wmf.WmfParser;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * file: org.ventry.docx.WmfConverter
 * author: ventry
 * create: 18/5/19 15:31
 * description:
 */

final class WmfConverter {

    private WmfConverter() {
        // Don't care this.
    }

    static OutputStream convert(InputStream inputStream) throws IOException {
        try {
            WmfParser parser = new WmfParser();
            final SvgGdi svgGdi = new SvgGdi(false);
            parser.parse(inputStream, svgGdi);
            try (ByteArrayOutputStream svgStream = new ByteArrayOutputStream()) {
                // Convert wmf to svg.
                readSvgStream(svgGdi.getDocument(), svgStream);
                // Convert svg to png.
                ImageTranscoder it = new PNGTranscoder();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgStream.toByteArray()));
                TranscoderOutput output = new TranscoderOutput(outputStream);
                it.transcode(input, output);
                return outputStream;
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    private static void readSvgStream(Document document, OutputStream outputStream) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD SVG 1.0//EN");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                "http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd");
        transformer.transform(new DOMSource(document), new StreamResult(outputStream));
    }
}