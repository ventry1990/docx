package org.ventry.docx;

/**
 * file: org.ventry.docx.XmlNamespace
 * author: ventry
 * create: 18/5/24 13:17
 * description:
 */
public enum XmlNamespace {
    A("a='http://schemas.openxmlformats.org/drawingml/2006/main';"),
    WP("wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing';"),
    V("v='urn:schemas-microsoft-com:vml';");

    private final String declare;
    private final String namespace;

    XmlNamespace(String ns) {
        declare = "declare namespace";
        namespace = ns;
    }

    public String text() {
        return declare + " " + namespace;
    }
}
