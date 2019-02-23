package org.ventry.docx.formula;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

/**
 * file: org.ventry.docx.formula.MathMLConverterTest
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
                Assert.assertEquals("In the case provided by " + file.getName() + ", the validation fails.",
                        expected, actual);
            } catch (IOException ioe) {
                Assert.fail(ioe.getMessage());
            }
        });
    }

    private CharSequence readTestInput(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
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