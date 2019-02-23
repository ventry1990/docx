package org.ventry.docx;

import org.junit.Assert;
import org.junit.Test;
import org.ventry.docx.picture.PictureProcessor;

import java.io.*;

/**
 * file: org.ventry.docx.DocxReaderTest
 * author: ventry
 * create: 18/5/19 16:10
 * description:
 */
public class DocxReaderTest {

    @Test
    public void read() throws Exception {
        try (InputStream stream = new FileInputStream(getClass().getResource("/demo.docx").getFile())) {
            String text = new DocxReader.Builder()
                    .setDocumentStream(stream)
                    .setPictureProcessor(new DownloadToFolder())
                    .build()
                    .read();
            System.out.println(text);
            Assert.assertNotNull(text);
        }
    }

    static class DownloadToFolder implements PictureProcessor {
        private String path;

        DownloadToFolder() throws IOException {
            String root = DownloadToFolder.class.getResource("/").getPath();
            File dir = new File(root + File.separator + "img");
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    throw new IOException("Failed to create image directory.");
                }
            }
            this.path = dir.getCanonicalPath();
        }

        public String process(String name, OutputStream outputStream) throws IOException {
            File saveFile = new File(path + File.separator + name);
            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                fos.write(((ByteArrayOutputStream) outputStream).toByteArray());
                return saveFile.getCanonicalPath();
            }
        }
    }
}