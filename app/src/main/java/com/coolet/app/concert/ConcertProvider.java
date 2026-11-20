package com.coolet.app.concert;

import java.util.List;

/** Contract for future licensed/official live-concert data integrations. */
public interface ConcertProvider {
    /** Provider identifier, e.g. an official event-feed integration. */
    String id();

    /** Search may return an empty list; never fabricate concerts. */
    List<Concert> search(ConcertRequestParser.Request request) throws Exception;

    final class Concert {
        public final String artist;
        public final String venue;
        public final String city;
        public final String startsAtIso;
        public final String timezone;
        public final String officialEventUrl;
        public final String source;
        public final long checkedAtEpochMillis;

        public Concert(String artist, String venue, String city, String startsAtIso,
                       String timezone, String officialEventUrl, String source,
                       long checkedAtEpochMillis) {
            if (artist == null || venue == null || city == null ||
                startsAtIso == null || officialEventUrl == null ||
                !officialEventUrl.startsWith("https://")) {
                throw new IllegalArgumentException("Missing required verified event fields");
            }
            this.artist = artist;
            this.venue = venue;
            this.city = city;
            this.startsAtIso = startsAtIso;
            this.timezone = timezone;
            this.officialEventUrl = officialEventUrl;
            this.source = source;
            this.checkedAtEpochMillis = checkedAtEpochMillis;
        }
    }
}
