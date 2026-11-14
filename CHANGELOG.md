# Changelog

## [2.0.0] - 2026-11-12

### Changed
- Transferred and consolidated `nimbus2-android-app` into `Tianxia-Core/tianxia-mobile`, purging 27 MB of committed Cargo `target/` and `build/` artifacts from repository history.
- Re-architected `crates/aether_mesh` (`v2.0.0`) and `crates/avf_core` (`v2.0.0`) into a clean Cargo workspace with bounded kill-switches, non-root fallbacks, `--health` / `--self-test` modes, and unit tests.
- Upgraded `AndroidManifest.xml`, `MainActivity`, and `SovereignHypervisorService` for Android SDK 34 (`arm64-v8a`), loopback-only socket binding, pause-aware UI refresh, and 256 KiB log rotation.

### Added
- Cross-platform static Go `tx-mobile-bridge` (`linux-x86_64`, `linux-aarch64`, `windows-x86_64.exe`, `android-aarch64`) with HMAC-SHA256 frame signing.
- Signed `tianxia-mobile-v2.0.0-android-arm64.apk` (`apksigner` v2/v3) + Ed25519 release signatures, `SHA256SUMS`, and CycloneDX `SBOM.cdx.json`.
