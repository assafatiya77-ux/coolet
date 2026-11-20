package com.coolet.app.concert;

import java.util.Locale;

/**
 * First offline building block for Coolet's concert agent.
 * Extracts city and genre from a spoken or typed request without network access.
 * Dates, ticket availability, prices and checkout are NOT inferred or verified here.
 */
public final class ConcertRequestParser {
    private ConcertRequestParser() {}

    public static final class Request {
        public final String originalText;
        public final String city;
        public final String genre;

        Request(String originalText, String city, String genre) {
            this.originalText = originalText;
            this.city = city;
            this.genre = genre;
        }

        public boolean needsCity() { return city == null; }
    }

    public static Request parse(String input) {
        String original = input == null ? "" : input.trim();
        String q = original.toLowerCase(Locale.ROOT);
        String city = null;
        if (containsAny(q, "שיקגו", "chicago")) city = "Chicago";
        else if (containsAny(q, "ברלין", "berlin")) city = "Berlin";
        else if (containsAny(q, "ניו יורק", "ניו-יורק", "new york", "nyc")) city = "New York";
        else if (containsAny(q, "תל אביב", "תל-אביב", "tel aviv", "tel-aviv")) city = "Tel Aviv";

        String genre = null;
        if (containsAny(q, "בלוז", "blues")) genre = "blues";
        else if (containsAny(q, "ג'אז", "ג׳אז", "jazz")) genre = "jazz";
        else if (containsAny(q, "רוק", "rock")) genre = "rock";
        else if (containsAny(q, "פאנק", "funk")) genre = "funk";
        else if (containsAny(q, "קאנטרי", "country")) genre = "country";
        return new Request(original, city, genre);
    }

    private static boolean containsAny(String input, String... terms) {
        for (String term : terms) if (input.contains(term)) return true;
        return false;
    }
}
