package com.coolet.app;
import java.text.Normalizer;
import java.util.Locale;

/** Local text matching, not AI media understanding. */
final class MediaSearch {
    private MediaSearch() {}
    static String normalize(String text) {
        if (text == null) return "";
        String decomposed = Normalizer.normalize(text, Normalizer.Form.NFD);
        StringBuilder result = new StringBuilder();
        decomposed.codePoints().forEach(cp -> {
            if ((cp >= 0x0591 && cp <= 0x05BD) || cp == 0x05BF
                    || cp == 0x05C1 || cp == 0x05C2 || cp == 0x05C4
                    || cp == 0x05C5 || cp == 0x05C7) return;
            if (Character.isWhitespace(cp) || Character.isSpaceChar(cp)) result.append(' ');
            else result.appendCodePoint(cp);
        });
        return Normalizer.normalize(result.toString(), Normalizer.Form.NFC)
                .toLowerCase(Locale.ROOT).trim();
    }
    static String[] terms(String query) {
        String normalized = normalize(query);
        return normalized.isEmpty() ? new String[0] : normalized.split("\\s+");
    }
    static boolean matches(String name, String description, String[] terms) {
        String haystack = normalize(name) + "\n" + normalize(description);
        for (String term : terms) if (!haystack.contains(term)) return false;
        return true;
    }
}
