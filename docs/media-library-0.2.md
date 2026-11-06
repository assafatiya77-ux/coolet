# Local media library 0.2
This change replaces the placeholder screen with user-selected photos and videos using Android's document picker. Selections are catalogued privately on the device, and read access is retained across restarts when the document provider supports it. Original files are not copied, moved, or deleted. Deleting an original or revoking access may make it unavailable in Coolet.

## Included
- Multiple selection of photos and videos.
- Persistent local catalog with atomic writes and serial background I/O.
- Filename search, opening selected media through an installed viewer.
- Hebrew (RTL) and English according to device language.
- Skip an already indexed URI; report added, skipped and failed selections.
- No broad storage or network permissions requested.

## Validation
CI runs Android lint and builds a debug APK. Compilation is not evidence that device behavior passed.
Device checks still required:
1. On a clean install, confirm the empty library and cancel the picker without changes.
2. Select two photos and a video together; confirm names and media types.
3. Open one image and one video with an installed viewer.
4. Force-stop and reopen Coolet; confirm catalog and access remain.
5. Re-select the same documents; verify no additional URI entries.
6. Rotate during import, then reopen; verify saved entries and no data loss.
7. Search Hebrew/English names and a nonexistent name.
8. Delete one original or revoke its document permission; verify the viewer reports unavailable media without losing other catalog entries.
9. Check Hebrew RTL, English LTR, large font size, Android 8 and Android 15 system bars.
10. Try a larger batch; record provider permission limits and per-item failures. 1,000-file capacity is not verified.

## Preserved roadmap (not implemented by this change)
Semantic/voice/transcript/image search, AI tagging, content-based duplicate detection, bulk WhatsApp group import, automatic discovery, collections/social/creator and learning features, Coolet Kids, highlight videos and photo books remain planned. The filename filter and URI re-selection check are not semantic search or content duplicate detection.
