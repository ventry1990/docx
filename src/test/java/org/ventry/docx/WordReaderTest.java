package org.ventry.docx;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;

/**
 * file: org.ventry.docx.WordReaderTest
 * author: ventry
 * create: 18/5/19 16:10
 * description:
 */
public class WordReaderTest {

    @Test
    public void read() throws Exception {
        String text = new WordReader(
                new FileInputStream(getClass().getResource("/demo.docx").getFile()),
                new DownloadToFolder(getClass().getResource("/img/").getPath())
        ).read();
        System.out.println(text);
        Assert.assertNotNull(text);
    }
}