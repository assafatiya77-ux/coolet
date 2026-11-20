package com.coolet.app.concert;

/**
 * Dependency-free parser smoke tests. Run with assertions enabled (-ea).
 * Punk (פאנק רוק) and funk (פאנק) must remain separate music genres.
 */
public final class ConcertRequestParserSmokeTest {
    private ConcertRequestParserSmokeTest() {}

    public static void main(String[] args) {
        check("תמצאי אנדרגראונד היפ הופ בשיקגו", "Chicago", "underground hip-hop");
        check("Underground hip-hop in Chicago", "Chicago", "underground hip-hop");
        check("הופעת פאנק רוק בברלין", "Berlin", "punk");
        check("punk rock in Berlin", "Berlin", "punk");
        check("מוזיקת פאנק בשיקגו", "Chicago", "funk");
        check("funk in New York", "New York", "funk");
        check("boom bap in NYC", "New York", "boom bap");
        check("jazz rap in Tel Aviv", "Tel Aviv", "jazz rap");
    }

    private static void check(String text, String expectedCity, String expectedGenre) {
        ConcertRequestParser.Request request = ConcertRequestParser.parse(text);
        if (!expectedCity.equals(request.city) || !expectedGenre.equals(request.genre)) {
            throw new AssertionError("For: " + text + ", expected " + expectedCity + "/"
                    + expectedGenre + ", got " + request.city + "/" + request.genre);
        }
    }
}
