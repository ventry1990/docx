package org.ventry.docx;

import org.junit.Assert;
import org.junit.Test;
import org.ventry.docx.picture.PictureProcessor;

import java.io.*;

/**
 * file: org.ventry.docx.WordReaderImplTest
 * author: ventry
 * create: 18/5/19 16:10
 * description:
 */
public class WordReaderImplTest {

    @Test
    public void read() throws Exception {
        String text = new WordReaderImpl(
                new FileInputStream(getClass().getResource("/demo.docx").getFile()),
                new DownloadToFolder(getClass().getResource("/img/").getPath())
        ).read();
        System.out.println(text);
        Assert.assertNotNull(text);
    }

    static class DownloadToFolder implements PictureProcessor {
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
}