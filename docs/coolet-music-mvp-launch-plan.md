# Coolet Music — launch MVP and proof-of-working checklist

## Product loop
Discover a real show -> open venue card -> official ticket page -> attend -> create an I WAS THERE punk card -> share with friends -> friends discover venue and upcoming shows.

## Release 1 scope
- NYC first: curated active Manhattan and Williamsburg clubs (Union Pool flagship). Avoid duplicate venue cards.
- City deck, venue cards, Joker surprise, show cards with event date, source, last verified time and external ticket link.
- Original black/white/red punk fanzine art direction; retain Coolet logo, accessible Hebrew/English UI.
- Android media picker to create a **private local** I WAS THERE card with venue, artist, date and photo; Android share sheet can export the image only after user action.
- No public uploads until moderation, accounts, reporting and rights workflow are implemented and tested.

## Release 2
- Secure public photo/video submissions, moderation, attribution, deletion, venue/band profiles and galleries.
- Additional cities after NYC end-to-end experience passes tests.

## Data integrity
- Integrate official/licensed event sources; don't show sample event fixtures as live. Show verified update timestamp and gracefully handle no events.
- Ticket buttons must point to genuine event or official venue pages; no invented URLs or prices.
- Avoid unauthorized scraping and copyright misuse.

## Acceptance tests
1. On an Android device, open city deck and venue card without crashes.
2. Open a current event with source and date, and tap through to a legitimate ticket page.
3. Generate a private I WAS THERE card from a phone photo; user can save/share explicitly.
4. No automatic public publishing; permissions minimal; Hebrew RTL works.
5. Build APK in CI and install/test on Android.

## Launch measurement
Track opt-in weekly active users, successful show-to-ticket taps, cards created, voluntary shares and repeat visits; no promises of virality.

## Actual status (2026-11-20)
Planning and venue JSON files committed in development branch. This plan is not an APK, event integration, or working upload system. Next engineering task: inspect Android project and implement first deck/card screen.
