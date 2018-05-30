package org.ventry.docx;

/**
 * file: org.ventry.docx.XmlNamespace
 * author: ventry
 * create: 18/5/24 13:17
 * description:
 */
public enum XmlNamespace {
    A("a='http://schemas.openxmlformats.org/drawingml/2006/main';"),
    M("m='http://schemas.openxmlformats.org/officeDocument/2006/main';"),
    V("v='urn:schemas-microsoft-com:vml';"),
    W("w='http://schemas.openxmlformats.org/wordprocessingml/2006/main';"),
    WP("wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing';");

    private static final String PREFIX = "declare namespace";
    private final String namespace;

    XmlNamespace(String ns) {
        namespace = ns;
    }

    public String text() {
        return PREFIX + " " + namespace;
    }

    public static String concat(XmlNamespace... namespaces) {
        if (namespaces == null) {
            return "";
        }

        StringBuilder declared = new StringBuilder();
        for (XmlNamespace xmlNamespace : namespaces) {
            declared.append(PREFIX)
                    .append(" ")
                    .append(xmlNamespace.namespace);
        }
        return declared.toString();
    }
}
