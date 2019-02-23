package org.ventry.docx.formula;


import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * file: org.ventry.docx.formula.LatexSymbols
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
        } catch (Exception e) {
            System.err.println("Cannot load latex symbols: " + e);
        }
    }

    private void loadSymbols() throws Exception {
        URL symbols = getClass().getClassLoader().getResource("symbols");
        if (symbols == null) {
            throw new IOException("/symbols doesn't exist");
        }

        if ("file".equals(symbols.getProtocol())) {
            loadLocalSymbols(symbols);
        } else if ("jar".equals(symbols.getProtocol())) {
            loadJarSymbols(symbols);
        } else {
            throw new IOException("/symbols cannot be recognized");
        }
    }

    private void loadLocalSymbols(URL symbols) throws Exception {
        File[] files = new File(symbols.toURI()).listFiles();
        if (files == null) {
            throw new IOException("/symbols/* doesn't exist");
        }

        for (File file : files) {
            loadSymbolsFrom(new FileInputStream(file));
        }
    }

    private void loadJarSymbols(URL symbols) throws Exception {
        JarURLConnection connection = (JarURLConnection) symbols.openConnection();
        JarFile jarFile = connection.getJarFile();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            String entryName = entries.nextElement().getName();
            if (entryName.contains("symbols/") && !entryName.endsWith("symbols/")) {
                URL url = new URL("jar:file:" + jarFile.getName() + "!/" + entryName);
                loadSymbolsFrom(url.openStream());
            }
        }
    }

    private void loadSymbolsFrom(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
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