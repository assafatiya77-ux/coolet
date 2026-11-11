package com.coolet.app;
public final class MediaSearchTest {
    private static int checked;
    private static void check(boolean expected, String name, String description, String query) {
        if (MediaSearch.matches(name, description, MediaSearch.terms(query)) != expected)
            throw new AssertionError("Query failed: " + query);
        checked++;
    }
    public static void main(String[] args) {
        check(true, "IMG.jpg", "טיול משפחתי בדרום", "משפחתי טיול");
        check(true, "טיול.jpg", "משפחה בדרום", "דרום טיול");
        check(true, "IMG.jpg", "שָׁלוֹם", "שלום");
        check(true, "IMG.jpg", "שלום", "שָׁלוֹם");
        check(true, "Trip.MP4", "Family holiday", "HOLIDAY trip");
        check(true, "Trip.MP4", "Family holiday", "  trip\u00a0holiday  ");
        check(false, "Trip.MP4", "Family holiday", "trip tomatoes");
        check(true, "Trip.MP4", "", "");
        check(true, "Trip.MP4", "", "   ");
        check(true, "קיץ.mp4", "Summer family", "family קיץ");
        check(false, "Trip.MP4", "Family holiday", "טיול");
        check(true, "café.jpg", "", "cafe\u0301");
        check(false, "café.jpg", "", "cafe");
        check(true, null, null, "");
        System.out.println("Passed " + checked + " search checks");
    }
}
