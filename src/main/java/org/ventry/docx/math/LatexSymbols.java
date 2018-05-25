package org.ventry.docx.math;


import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * file: org.ventry.docx.math.LatexSymbols
 * author: ventry
 * create: 18/5/23 17:14
 * description:
 */

public enum LatexSymbols {
    DICTIONARY;

    private final Map<String, String> symbols = new HashMap<>();

    LatexSymbols() {
        try {
            loadSymbols();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSymbols() throws IOException {
        File[] files = getSymbolFiles();
        for (File file : files) {
            loadSymbolsFrom(file);
        }
    }

    private File[] getSymbolFiles() throws IOException {
        URL symbols = getClass().getClassLoader().getResource("symbols");
        if (symbols == null) {
            throw new IOException("/symbols doesn't exist.");
        }

        File dir = Paths.get(symbols.getPath()).toFile();
        if (!dir.isDirectory()) {
            throw new IOException("/symbols isn't a directory");
        }

        return dir.listFiles();
    }

    private void loadSymbolsFrom(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file)))) {
            String symbol;
            while ((symbol = reader.readLine()) != null) {
                if (symbol.startsWith("#") || !symbol.contains("|")) {
                    continue;
                }

                String[] pair = symbol.split("\\|");
                symbols.put(translate(pair[0]), pair[1]);
            }
        }
    }

    private String translate(String str) {
        return StringEscapeUtils.unescapeHtml4(str);
    }

    public String getOrDefault(String el, String def) {
        final String key = translate(el);
        if (symbols.containsKey(key)) {
            return symbols.get(key);
        }
        return def;
    }
}