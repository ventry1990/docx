package org.ventry.docx;

import org.apache.xmlbeans.XmlObject;

/**
 * file: org.ventry.docx.ContentReader
 * author: ventry
 * create: 18/5/30 10:51
 * description:
 */

public interface ContentReader {

    boolean match(XmlObject object);

    CharSequence read(XmlObject object);
}