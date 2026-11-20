package com.coolet.app.concert;

import java.util.Locale;

/**
 * Offline intent extraction for Coolet concert discovery.
 * Does not claim live events, ticket availability, prices, or booking.
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
        // More specific subgenres must precede their parent genre.
        if (containsAny(q, "אנדרגראונד היפ הופ", "אנדרגראונד היפ-הופ",
                "היפ הופ מחתרתי", "היפ-הופ מחתרתי",
                "underground hip-hop", "underground hip hop", "underground rap"))
            genre = "underground hip-hop";
        else if (containsAny(q, "בום באפ", "boom bap", "boom-bap"))
            genre = "boom bap";
        else if (containsAny(q, "ג'אז ראפ", "ג׳אז ראפ", "jazz rap", "jazz-rap"))
            genre = "jazz rap";
        else if (containsAny(q, "אלטרנטיב היפ הופ", "alternative hip-hop", "alternative hip hop"))
            genre = "alternative hip-hop";
        else if (containsAny(q, "ראפ באטל", "rap battle", "rap battles"))
            genre = "rap battle";
        else if (containsAny(q, "היפ הופ", "היפ-הופ", "hip-hop", "hip hop", "ראפ", " rap"))
            genre = "hip-hop";
        else if (containsAny(q, "בלוז", "blues")) genre = "blues";
        else if (containsAny(q, "ג'אז", "ג׳אז", "jazz")) genre = "jazz";
        else if (containsAny(q, "פאנק רוק", "punk rock", "פאנק-רוק", "punk", "פאנק מחתרתי"))
            genre = "punk";
        else if (containsAny(q, "פאנק", "funk")) genre = "funk";
        else if (containsAny(q, "טכנו", "techno")) genre = "techno";
        else if (containsAny(q, "קאנטרי", "country")) genre = "country";
        else if (containsAny(q, "רוק", "rock")) genre = "rock";
        return new Request(original, city, genre);
    }

    private static boolean containsAny(String input, String... terms) {
        for (String term : terms) if (input.contains(term)) return true;
        return false;
    }
}
