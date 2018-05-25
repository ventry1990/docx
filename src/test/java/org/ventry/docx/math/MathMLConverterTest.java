package org.ventry.docx.math;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * file: org.ventry.docx.math.MathMLConverterTest
 * author: ventry
 * create: 18/5/25 17:13
 * description:
 */
public class MathMLConverterTest {

    @Test
    public void convert() throws Exception {
        File dir = new File(getClass().getResource("/mathml").toURI());
        File[] testFiles = dir.listFiles();
        if (testFiles == null) {
            Assert.fail("There are no test resources.");
        }

        Stream.of(testFiles).forEach(file -> {
            try {
                CharSequence input = readTestInput(file);
                MathMLConverter converter = new MathMLConverter(input);
                String actual = converter.convert();

                String expected = readExpectedOutput(file);
                Assert.assertEquals("In case provided by " + file.getName() + ", the validation fails.",
                        expected, actual);
            } catch (IOException ioe) {
                Assert.fail(ioe.getMessage());
            }
        });
    }

    private CharSequence readTestInput(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("<!--")) {
                    content.append(line);
                }
            }
            return content;
        }
    }

    private String readExpectedOutput(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("<!--")) {
                    return line.substring(4, line.length() - 3).trim();
                }
            }
            return "?";
        }
    }
}