package org.ventry.docx.formula;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.ventry.docx.ContentReader;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Optional;

/**
 * file: org.ventry.docx.formula.FormulaReader
 * author: ventry
 * create: 18/5/23 17:12
 * description:
 */

public class FormulaReader implements ContentReader {

    private static final ThreadLocal<Optional<Transformer>> LOCAL_TRANSFORMER = ThreadLocal.withInitial(() -> {
        String path = "xsl/omml2mml.xsl";
        try {
            URL styleUrl = FormulaReader.class.getClassLoader().getResource(path);
            if (styleUrl == null) {
                throw new FileNotFoundException(path);
            }

            StreamSource source = new StreamSource(styleUrl.toURI().toASCIIString());
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(source);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            return Optional.of(transformer);
        } catch (FileNotFoundException notFound) {
            System.err.println(path + " doesn't exist");
        } catch (Exception e) {
            System.err.println("Something wrong in FormulaReader.<clinit>(): " + e);
        }
        return Optional.empty();
    });

    public boolean match(XmlObject object) {
        return object instanceof CTOMath;
    }

    public CharSequence read(XmlObject object) {
        CTOMath oMath = (CTOMath) object;
        return LOCAL_TRANSFORMER.get().map(transformer -> {
            try {
                DOMSource source = new DOMSource(oMath.getDomNode());
                StringWriter mathML = new StringWriter();
                transformer.transform(source, new StreamResult(mathML));
                return new MathMLConverter(mathML.getBuffer()).convert();
            } catch (TransformerException te) {
                te.printStackTrace();
                return "";
            }
        }).orElse("");
    }
}