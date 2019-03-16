package org.ventry.docx.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.ventry.docx.formula.LatexSymbols.DICTIONARY;

/**
 * file: org.ventry.docx.formula.MathMLConverter
 * author: ventry
 * create: 18/5/24 13:26
 * description:
 */

class MathMLConverter {
    private final ContentWrapper wrapper;
    private final StringBuilder mml;
    private final StringBuilder section;
    private int cursor;

    MathMLConverter(CharSequence source) {
        wrapper = new ContentWrapper();
        mml = new StringBuilder(source);
        section = new StringBuilder(17);
        cursor = 0;
    }

    /**
     * Convert MathML to Latex
     */
    String convert() {
        List<CharSequence> symbols = convertRecursively(mml.length());
        String content = String.join(" ", symbols);
        return wrapper.wrap(content);
    }

    private List<CharSequence> convertRecursively(int endExclude) {
        List<CharSequence> siblings = new ArrayList<>();
        while (toNextSection(endExclude)) {
            String head = section.toString();
            SymbolTemplate tagSymbol = new SymbolTemplate(DICTIONARY.getOrDefault(head, ""));

            int end = findSectionEnd(head, endExclude);
            List<CharSequence> children = convertRecursively(end);

            boolean replaced = tagSymbol.replacePlaceholder(children);
            if (!replaced) {
                tagSymbol.append(children);
            }
            siblings.add(tagSymbol);
            cursor = end + head.length() + 3;
        }

        // Trim blanks and avoid dictionary miss, and calculating range
        // for substring rather than generate redundant string
        int from = skipBlank(cursor, endExclude);
        int to = skipBlankReversely(from, endExclude - 1);
        if (from < to) {
            String elementSymbol = mml.substring(from, to);
            siblings.add(DICTIONARY.getOrDefault(elementSymbol, elementSymbol));
        }
        return siblings;
    }

    private boolean toNextSection(int endExclude) {
        int i = cursor;
        while (i < endExclude && mml.charAt(i) != '<') {
            i++;
        }

        // Get the section and ignore it's attribution.
        if (i < endExclude) {
            section.setLength(0);

            i++;// Skip the '<'.
            char c;
            while (i < endExclude && (c = mml.charAt(i)) != '>'
                    && c != ' ') {
                section.append(c);
                i++;
            }

            while (i < endExclude && mml.charAt(i) != '>') {
                i++;
            }
            i++;// Skip the '>'.
        }

        boolean hasNextSection = i < endExclude;
        if (hasNextSection) {
            cursor = i;
        }
        return hasNextSection;
    }

    private int findSectionEnd(String section, int endExclude) {
        int count = 0;
        int from = cursor;
        while ((from = mml.indexOf(section, from + 1)) < endExclude && from > cursor) {
            if (mml.charAt(from - 1) == '/') {
                if (count == 0) {
                    return from - 2;
                } else {
                    count--;
                }
            } else if (mml.charAt(from - 1) == '<') {
                count++;
            }
        }
        return endExclude;
    }

    private int skipBlank(int startInclude, int endExclude) {
        int i = startInclude;
        while (i < endExclude && mml.charAt(i) == ' ') {
            i++;
        }
        return i;
    }

    private int skipBlankReversely(int startExclude, int endInclude) {
        int i = endInclude;
        while (i > startExclude && mml.charAt(i) == ' ') {
            i--;
        }
        return i + 1;
    }

    private static class ContentWrapper {
        private static final Pattern TEXT_WRAPPER = Pattern.compile("([\\u4e00-\\u9fff]+)");

        private String wrap(String content) {
            return TEXT_WRAPPER.matcher(content).replaceAll("\\\\text{$1}");
        }
    }
}