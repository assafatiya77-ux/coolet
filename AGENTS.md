# Coolet project working rules

## User rule — אין חצאי עבודות (No half-finished work)

Explicitly requested by the project owner on 2026-11-10. Applies to Coolet and Coolet Kids.

- Define the scope and observable acceptance criteria of a task or version before marking it complete. A bounded version is allowed; do not present that bounded version as the entire approved product.
- Carry an authorized task through implementation, appropriate build checks, end-to-end behavior verification, durable source/artifact preservation and a usable handoff. Fix detected defects within that scope instead of handing them back as finished work.
- A successful build, written code, mockup or isolated passing check is progress, not proof of a completed user workflow.
- Mark a feature/version DONE only when all acceptance criteria in its declared scope have evidence. Untested behavior, failures and blockers must remain explicitly open. Never relabel partial coverage as full completion or claim that every possible condition was tested.
- Record the exact version/commit/APK tested, test environment, passed checks and remaining limitations. Distinguish emulator checks from physical Android device checks and user reports from independently observed results.
- Continue independent permitted work without requiring the owner to navigate or approve routine reversible actions. Request user action only for evidence or access that cannot be obtained independently.
- Keep all approved roadmap features, decisions, code and backups. Do not delete originals or clear an installed app's data to work around an update failure.
- Verify package identity, version and signing compatibility before instructing an in-place Android update. Use the same securely retained signing key for successive previews; never put private keys or passwords in the repository or CI artifacts.
- Report accurately and briefly in Hebrew, including what is completed, what remains open and the next concrete action. Do not promise deadlines, records, superiority or capabilities without evidence.

## Current 0.2.3 boundary

Coolet Preview 0.2.3 is a separate application (com.coolet.app.preview), not an in-place update of Coolet 0.2. Media type filters, local manual descriptions and filename/description search are implemented. Semantic AI search, transcript/image search, content duplicate detection, whole-group WhatsApp import and Kids safety remain approved roadmap work, not completed capabilities.

Runtime acceptance remains open until its stated checks pass. Existing emulator installation/startup/control checks do not alone prove import, description persistence or actual media playback.
