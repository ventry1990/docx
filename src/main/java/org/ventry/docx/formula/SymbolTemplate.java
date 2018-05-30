package org.ventry.docx.formula;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * file: org.ventry.docx.formula.SymbolTemplate
 * author: ventry
 * create: 18/5/25 15:47
 * description:
 */

final class SymbolTemplate implements CharSequence {
    private final StringBuilder content;

    SymbolTemplate(CharSequence source) {
        this.content = new StringBuilder(source);
    }

    boolean replacePlaceholder(List<CharSequence> replacements) {
        boolean replaced = false;
        for (int i = 0; i < content.length(); ) {
            ReplaceStrategy strategy = replaceStrategy(i);
            switch (strategy) {
                case ONE:
                    int replaceIndex = content.charAt(i + 1) - '1';
                    content.delete(i, i + 2);
                    content.insert(i, replacements.get(replaceIndex));
                    i += replacements.get(replaceIndex).length();
                    replaced = true;
                    break;
                case ALL:
                    content.delete(i, i + 2);
                    String replacement = replacements.stream().collect(Collectors.joining(" "));
                    content.insert(i, replacement);
                    return true;
                case NONE:
                default:
                    i++;
                    break;
            }
        }

        return replaced;
    }

    private ReplaceStrategy replaceStrategy(int index) {
        boolean doReplace = isPlaceholderAt(index);
        if (!doReplace) {
            return ReplaceStrategy.NONE;
        }

        char next = content.charAt(index + 1);
        return (next > '0' && next <= '9') ? ReplaceStrategy.ONE : ReplaceStrategy.ALL;
    }

    private boolean isPlaceholderAt(int index) {
        if (content.charAt(index) != '%' || index >= content.length())
            return false;

        char next = content.charAt(index + 1);
        return (next > '0' && next <= '9') || next == 'S';
    }


    void append(List<CharSequence> rest) {
        if (CollectionUtils.isEmpty(rest)) {
            return;
        }

        if (content.length() == 0) {
            content.append(rest.get(0));
        }

        for (int i = 1; i < rest.size(); i++) {
            content.append(' ').append(rest.get(i));
        }
    }

    public int length() {
        return content.length();
    }

    public char charAt(int index) {
        return content.charAt(index);
    }

    public CharSequence subSequence(int start, int end) {
        return content.subSequence(start, end);
    }

    @Override
    public String toString() {
        return content.toString();
    }

    private enum ReplaceStrategy {
        NONE, ONE, ALL
    }
}