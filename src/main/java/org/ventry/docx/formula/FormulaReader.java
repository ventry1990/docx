package org.ventry.docx.formula;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.ventry.docx.ContentReadException;
import org.ventry.docx.ContentReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * file: org.ventry.docx.formula.FormulaReader
 * author: ventry
 * create: 18/5/23 17:12
 * description:
 */

public class FormulaReader implements ContentReader {
    private static final StreamSource STYLE_SOURCE;

    static {
        String stylePath = "xsl" + File.separator + "omml2mml.xsl";
        InputStream styleStream = FormulaReader.class.getClassLoader().getResourceAsStream(stylePath);
        STYLE_SOURCE = new StreamSource(styleStream);
    }

    public boolean match(XmlObject object) {
        return object instanceof CTOMath;
    }

    public CharSequence read(XmlObject object) throws ContentReadException {
        CTOMath oMath = (CTOMath) object;
        try {
            Transformer transformer = prepareTransformer();
            DOMSource source = new DOMSource(oMath.getDomNode());
            StringWriter mathML = new StringWriter();
            transformer.transform(source, new StreamResult(mathML));
            return new MathMLConverter(mathML.getBuffer()).convert();
        } catch (TransformerException te) {
            throw new ContentReadException(te.getMessage(), te);
        }
    }

    private Transformer prepareTransformer() throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(STYLE_SOURCE);
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        return transformer;
    }
}