package com.coolet.app;

import java.text.Normalizer;
import java.util.Locale;

/** Local keyword matching only; never changes the stored name or description. */
final class MediaSearch {
    private MediaSearch() {}

    static String normalize(String text) {
        String decomposed = Normalizer.normalize(text, Normalizer.Form.NFD)
                .toLowerCase(Locale.ROOT);
        StringBuilder result = new StringBuilder();
        boolean space = true;
        for (int offset = 0; offset < decomposed.length();) {
            int cp = decomposed.codePointAt(offset);
            offset += Character.charCount(cp);
            int type = Character.getType(cp);
            if (type == Character.NON_SPACING_MARK || type == Character.COMBINING_SPACING_MARK
                    || type == Character.ENCLOSING_MARK || type == Character.FORMAT) continue;
            if (Character.isLetterOrDigit(cp) || type == Character.OTHER_SYMBOL) {
                result.appendCodePoint(cp);
                space = false;
            } else if (!space) {
                result.append(' ');
                space = true;
            }
        }
        return result.toString().trim();
    }

    static String[] terms(String query) {
        String normalized = normalize(query);
        return normalized.isEmpty() ? new String[0] : normalized.split(" +");
    }

    static boolean matches(String name, String description, String[] terms) {
        String searchable = normalize(name) + " " + normalize(description);
        for (String term : terms) if (!searchable.contains(term)) return false;
        return true;
    }
}
