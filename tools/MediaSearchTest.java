package com.coolet.app;

import java.util.Locale;

public final class MediaSearchTest {
    private static int count;
    private static void check(boolean expected, String name, String description, String query) {
        boolean actual = MediaSearch.matches(name, description, MediaSearch.terms(query));
        if (actual != expected) throw new AssertionError("Unexpected result for query: " + query);
        count++;
    }
    public static void main(String[] args) {
        check(true, "IMG_2026.jpg", "טיול עם מורן", "מורן טיול");
        check(true, "IMG_2026.jpg", "טִיּוּל עִם מוֹרָן", "מורן טיול");
        check(true, "מורן.jpg", "טיול משפחתי", "טיול מורן");
        check(false, "מורן.jpg", "טיול משפחתי", "טיול סוסים");
        check(true, "Family_2026.JPG", "Trip in Budapest", "budapest FAMILY 2026");
        check(true, "Family_2026.JPG", "Trip in Budapest", "  family\u00a0\ttrip  ");
        check(true, "IMG.jpg", "café", "cafe");
        check(true, "IMG.jpg", "טיול\u200f", "טיול");
        check(true, "IMG.jpg", "family-trip", "trip family");
        check(true, "IMG.jpg", "family", "");
        check(true, "IMG.jpg", "family", " , - ");
        check(false, "IMG.jpg", "family", "missing");
        check(true, "IMG.jpg", "Family", "fam");
        check(true, "IMG.jpg", "🐎 ride", "🐎");
        check(false, "IMG.jpg", "ride", "🐎");
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        check(true, "IMAGE.JPG", "TRIP", "image trip");
        String original = "טִיּוּל";
        MediaSearch.normalize(original);
        if (!original.equals("טִיּוּל")) throw new AssertionError("Original altered");
        System.out.println("PASS: " + count + " keyword-search cases");
    }
}
