# Coolet Music — consolidated approved vision (20 Nov 2026)

Public app name remains **Coolet**; **Coolet Music** is internal project name. Preserve existing Android app, logo, code, APK work, security decisions and private-by-default approach. Android first, Hebrew and English.

## Product
- City decks of casino-style playing cards. Each card represents an active live music club, not a closed historical club. Joker reveals cheerful surprise music/artist/clip.
- First coverage: NYC Manhattan (SoHo, East Village, Union Square/Gramercy), Williamsburg Brooklyn including Union Pool near BQE, Hoboken NJ; later Chicago, Berlin, Tel Aviv, LA.
- Each venue card shows music, history, recommendations, upcoming verified live events, artists and ticket links. Obtain events from permitted official venue calendars and licensed/available ticketing APIs; never pretend stale sample entries are live. Deduplicate clubs across neighborhood datasets.
- Personal music/video collections, authorized YouTube embedded links, AI natural-language search, lessons and creators remain part of broader Coolet vision. Do not claim unfinished features are built.

## Community and iconic imagery
- **Iconic Moments / רגעים אייקוניים** gallery on venue and event cards: photos and short videos of audience, bands, venue and historical licensed imagery. Credit creators and distinguish curated historic archives from fan posts.
- **I WAS THERE / הייתי שם**: concertgoers take or select selfies, photos with friends, dancing/singing videos, and generate original shareable show cards with artist, venue and date.
- Bands upload band portraits, backstage, rehearsals, audience meetups; venues upload authorized material. Organize by concert, artist, club and city.
- Fans and artists can share public cards via social apps, linking back to Coolet and venue/event.
- Default private, public opt-in only, moderation before public visibility, consent/privacy of people pictured, rights confirmation, reporting/takedown, user deletion, attribution, copyright safeguards; minors need special safeguards.

## Art direction
- Original 1970s NYC punk/fanzine visual language inspired by the *era* of the book Please Kill Me: black, white, red; rough type, photocopy textures, torn concert flyers, collectible club cards, energetic Joker. Do not copy book cover/artwork. Keep existing Coolet logo. Mobile usability/accessibility must remain clear.

## Implementation order and verified status
1. Existing seed data stored on feature/nyc-live-music-sources branch: NYC, Williamsburg/Hoboken, East Village/SoHo, Manhattan additions, Union Square. These are data files, not working auto-sync.
2. Implement venue/event data model, source attribution, deduplication, live calendar integration and ticket links.
3. Implement Android city decks, club cards, event views and Joker.
4. Implement private Android media picker and I WAS THERE card, then secure uploads, accounts, moderation, public galleries and sharing.
5. Build APK, install/test on Android, fix errors; do not claim complete until verified.

No automatic calendar integration, public upload backend, moderation or finished APK for this redesign has been verified.
