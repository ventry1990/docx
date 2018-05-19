package org.ventry.docx;

import java.io.IOException;
import java.io.OutputStream;

/**
 * file: org.ventry.docx.PictureProcessor
 * author: ventry
 * create: 18/5/19 15:38
 * description:
 */
public interface PictureProcessor {
    String process(String name, OutputStream outputStream) throws IOException;
}
