package org.ventry.docx;

import java.io.*;

/**
 * file: org.ventry.docx.DownloadToFolder
 * author: ventry
 * create: 18/5/19 16:11
 * description:
 */

class DownloadToFolder implements PictureProcessor {
    private String path;

    DownloadToFolder(String dirPath) {
        this.path = dirPath;
    }

    public String process(String name, OutputStream outputStream) throws IOException {
        File saveFile = new File(path + File.separator + name);
        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
            fos.write(((ByteArrayOutputStream) outputStream).toByteArray());
            return saveFile.getCanonicalPath();
        }
    }
}