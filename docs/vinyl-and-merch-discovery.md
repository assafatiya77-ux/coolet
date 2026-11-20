# Coolet Music — vinyl and band merchandise discovery

Approved 2026-11-20. Product requirement only; not yet implemented.

## Band / show card
- Add **תקליטים / VINYL & MERCH** section to artist and concert cards.
- Link to verified official artist store, Bandcamp artist release/merch page, and Discogs release/marketplace search as applicable.
- Display release title, format (vinyl/CD/cassette), edition, price and availability **only when verified from an authorized source**; otherwise show 'לבדיקת זמינות באתר המוכר'.
- Clearly distinguish official artist sales from third-party/resale listings; indicate when link is external.
- No fabricated prices, stock, affiliate relationships or sales commissions.
- Prefer artist-direct sales when available, then reputable marketplaces. Respect platform terms, trademarks, copyright, and privacy.
- Initial MVP: outbound links on artist/venue cards, no payments, inventory, shipping or checkout inside Coolet.
- Later: affiliate partnerships only after disclosure and verified eligibility.

## User flow
Discover show -> artist card -> listen through authorized embed -> VINYL & MERCH -> external seller -> optionally return to Coolet for event tickets and I WAS THERE cards.

## Implementation
1. Extend artist metadata with optional verified official_store_url, bandcamp_url and discogs_url; validate allowed https URLs.
2. Add external-link UI to Android artist/event cards.
3. Test links on Android and add 'report broken link' affordance.
