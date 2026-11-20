# Coolet Live — MVP implementation plan

Public app name: Coolet. First section: Coolet Live.
Preserve existing Android app, gallery/media import, private collections and code. Work only on this feature branch until tested.

## Launch coverage
- Berlin, Germany (first data collection and QA)
- Tel Aviv, Israel
- New York City, USA
- Next: Chicago, USA
- Expand Israel coverage to Haifa, Beersheba and kibbutzim as verified listings become available.

## User-facing minimum
1. Find concerts by city, date, and genre (punk, rockabilly, rock'n'roll, blues, jazz, indie).
2. Show event title, performer, venue, local date/time, source URL, ticket URL, last verified time.
3. Filter and save favorites privately. Show a clear source link; never fabricate live listings.
4. Free access. Paid promoted listings for bands/venues later, always visibly labeled sponsored.
5. Casino-style city decks and Joker remain an approved visual/discovery enhancement, but fast list/search must remain available.

## Data model
Venue: id, name, city, country, address, genres, official_website, schedule_url, verified_at.
Event: id, venue_id, artist, title, start_local, timezone, genres, source_url, ticket_url, last_checked_at, status.
Promotion: event_id, sponsor, start_at, end_at, payment_status, sponsored_label.

## Engineering gates
- Inspect existing MainActivity and Android build before editing app code.
- Keep existing code and main branch unchanged.
- First build a working list/filter screen with a small verified seed dataset.
- Do not claim automated updates without an implemented permitted source integration and freshness checks.
- Build debug APK in GitHub Actions, install on Android, test navigation, links, filtering and persistence.
- Public release only after copyright, privacy, security, accessibility and store-policy checks.

## Monetization
Free for users. Possible later: clearly marked paid band/venue listings, ads and verified ticket referral commissions. Prices and revenue are unvalidated; do not charge until payments and disclosure are implemented.
