package org.ventry.docx.math;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.ventry.docx.math.LatexSymbols.DICTIONARY;

/**
 * file: org.ventry.docx.math.MathMLConverter
 * author: ventry
 * create: 18/5/24 13:26
 * description:
 */

class MathMLConverter {
    private final StringBuilder mml;
    private final StringBuilder section;
    private int cursor;

    MathMLConverter(CharSequence source) {
        this.mml = new StringBuilder(source);
        this.section = new StringBuilder(17);
        this.cursor = 0;
    }

    /**
     * Convert MathML to Latex
     */
    String convert() {
        List<CharSequence> symbols = convertRecursively(mml.length());
        return symbols.stream().collect(Collectors.joining(" "));
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
        while ((from = mml.indexOf(section, from + 1)) < endExclude) {
            if (mml.charAt(from - 1) == '/') {
                if (count == 0) {
                    return from - 2;
                } else {
                    count--;
                }
            } else {
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
}