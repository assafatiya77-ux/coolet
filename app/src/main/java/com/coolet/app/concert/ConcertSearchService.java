package com.coolet.app.concert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Provider-agnostic concert search coordinator.
 * Real results are returned only by configured providers; no fabricated fallback.
 */
public final class ConcertSearchService {
    private final List<ConcertProvider> providers;

    public ConcertSearchService(List<ConcertProvider> providers) {
        if (providers == null) throw new IllegalArgumentException("providers required");
        this.providers = new ArrayList<>(providers);
    }

    public Result search(String userRequest) {
        ConcertRequestParser.Request parsed = ConcertRequestParser.parse(userRequest);
        List<ConcertProvider.Concert> events = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        if (parsed.needsCity()) {
            errors.add("Please specify a city: Chicago, Berlin, New York or Tel Aviv.");
            return new Result(events, errors);
        }
        Set<String> seen = new HashSet<>();
        for (ConcertProvider provider : providers) {
            try {
                List<ConcertProvider.Concert> found = provider.search(parsed);
                if (found == null) continue;
                for (ConcertProvider.Concert event : found) {
                    if (event == null || !event.city.equalsIgnoreCase(parsed.city)) continue;
                    String key = (event.artist + "|" + event.venue + "|" +
                            event.startsAtIso).toLowerCase(Locale.ROOT);
                    if (seen.add(key)) events.add(event);
                }
            } catch (Exception e) {
                errors.add("Concert provider " + provider.id() + " unavailable");
            }
        }
        return new Result(events, errors);
    }

    public static final class Result {
        public final List<ConcertProvider.Concert> events;
        public final List<String> errors;
        Result(List<ConcertProvider.Concert> events, List<String> errors) {
            this.events = events;
            this.errors = errors;
        }
    }
}
