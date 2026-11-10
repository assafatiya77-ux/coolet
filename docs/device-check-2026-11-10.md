# Android device check — 2026-11-10

Reported by the user during a guided session; no direct device access or recording.

Version 0.2: update installed; app launched; image opened through external viewer; video played; mixed media remained listed after closing and reopening. Exact Android version, device identity, force-stop behavior and batch size were not independently verified.

The user requested separate views for photos and videos. Version 0.2.1 adds All / Photos / Videos filters combined with filename search, and retains filter selection during Activity recreation. Existing catalog format remains compatible.

Pending 0.2.1 checks: install update without uninstalling; confirm existing media remains; switch all three filters; search within a filter; rotate and confirm filter selection; open image and video after restart.

Still pending for 0.2: duplicate URI selection, large batches, provider permission limits, revoked access, rotation during import, accessibility and multiple Android versions.

Semantic search, transcript/image search, content duplicate detection and whole-group WhatsApp import remain unimplemented.

## Version 0.2.2
Adds optional manual descriptions (up to 1,000 characters) via long press. Filename and description search combine with photo/video filters. Stored descriptions are local catalog metadata; original files are not renamed or modified. Existing 0.2 catalogs load with an empty description. Saves use serial background I/O and atomic catalog replacement.

Device validation pending: add Hebrew and English descriptions, cancel without saving, clear an existing description, search descriptions within a media type, reopen and confirm persistence, update over an existing catalog. This is manual annotation, not semantic or AI content search.

## Signing failure and preview 0.2.3
The user showed installed version 0.2 and reported update rejection. The delivered 0.2 and 0.2.2 APK certificate SHA256 values differ: d248cec376b83122059b72fb47ef44867852aaa0e2456fd7a7c24567cfcedd9e vs 1d7224205a514a283f890ae4ea929fc4727169c998e61b0946a042fd0c55b3b8. CI generated temporary debug keys on separate runners; no original private signing key is available.

Preview 0.2.3 uses applicationId com.coolet.app.preview and label Coolet Preview so it can coexist with the installed app. It starts with a separate empty catalog; media must be selected again. Never instruct uninstalling or clearing the original app. It adds a visible version label and Description buttons.

CI produces an unsigned release APK. Distributed previews must be signed outside CI with the privately retained preview key. Never publish signing keys/passwords to the repository or Actions artifacts. This does not restore update compatibility with com.coolet.app. Device installation remains pending.
