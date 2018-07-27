package org.ventry.docx.text;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.ventry.docx.ContentReader;

/**
 * file: org.ventry.docx.text.TextReader
 * author: ventry
 * create: 18/5/30 10:57
 * description:
 */

public class TextReader implements ContentReader {

    public boolean match(XmlObject object) {
        return object instanceof CTText;
    }

    public CharSequence read(XmlObject object) {
        return ((CTText) object).getStringValue();
    }
}