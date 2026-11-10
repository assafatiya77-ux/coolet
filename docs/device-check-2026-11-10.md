# Android device check — 2026-11-10

Reported by the user during a guided session; no direct device access or recording.

Version 0.2: update installed; app launched; image opened through external viewer; video played; mixed media remained listed after closing and reopening. Exact Android version, device identity, force-stop behavior and batch size were not independently verified.

The user requested separate views for photos and videos. Version 0.2.1 adds All / Photos / Videos filters combined with filename search, and retains filter selection during Activity recreation. Existing catalog format remains compatible.

Pending 0.2.1 checks: install update without uninstalling; confirm existing media remains; switch all three filters; search within a filter; rotate and confirm filter selection; open image and video after restart.

Still pending for 0.2: duplicate URI selection, large batches, provider permission limits, revoked access, rotation during import, accessibility and multiple Android versions.

Semantic search, transcript/image search, content duplicate detection and whole-group WhatsApp import remain unimplemented.
