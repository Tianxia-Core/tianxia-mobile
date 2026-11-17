<p align="center">
  <img src="./brand-kit/01-logos/lockups-and-social/dual-lockup-rxm-x-tianxiaos.svg" alt="RASA-X-MACHINA · tianxiaOS" width="680"/>
</p>

# tianxia-mobile

**Sovereign Mobile Client, AetherMesh Node Daemon & Cross-Platform Host Bridge**

`tianxia-mobile` unifies the Android `arm64-v8a` mobile client (`com.rasaxmachina.tianxia`), the Rust `aether_mesh` (`aarch64-linux-android`) and `avf_nomad_core` edge daemons, and the static Go `tx-mobile-bridge` host relay across Linux, Windows, and Android.

<!-- BUILD_LAW_SHOWCASE_START -->

---

## 🌍 In Plain English — What `tianxia-mobile` Is & Why It Matters (Layman's Guide)

- **The Everyday Problem:** Smartphones carry desktop-class ARM64 processors in our pockets, yet almost every developer tool treats mobile devices as passive dumb terminals tethered to someone else's cloud.
- **How It Works (Without the Jargon):** `tianxia-mobile` turns Android (`arm64-v8a`) devices into first-class encrypted mesh nodes with battery-aware sleep coalescing (`Annapūrṇā`), signed native ARM64 binaries, and a verified Android `.apk` cockpit.
- **Why It Is Different:** Engineered under **[Build Law](https://github.com/Tianxia-Core/Build-Law)** (`MPL-2.0`) so mobile nodes participate securely in zero-trust WireGuard/Tailscale clusters without draining battery.
- **How It Benefits You:** Operators and field engineers can monitor, verify, and orchestrate sovereign infrastructure from their pocket anywhere on Earth.
- **How to Install & Use It in 60 Seconds:** Download the `apksigner` v2/v3 verified `.apk` or ARM64 CLI tarball from **[Latest Releases](https://github.com/Tianxia-Core/tianxia-mobile/releases/latest)**.

---

## 🏛️ Governed by [`Build Law`](https://github.com/Tianxia-Core/Build-Law) — Engineering Philosophy & Stakeholder Pathways

This repository is a live production showcase of **[`Build Law`](https://github.com/Tianxia-Core/Build-Law)**—the open-source (`MIT`) **4-Pillar, 14-Gate Constitutional Engineering & `5.0×–8.8×` Token-Efficiency Standard** created by **[Collins Takem](https://rasaxmachina.com/collins)** (**RASA-X-MACHINA**):
- **Developers & AI Builders:** Drop **[`Build Law`](https://github.com/Tianxia-Core/Build-Law)** into Claude Code, Cursor, OpenCode, VS Code (`.vsix`), GitHub Copilot, Windsurf, Cline, Aider, or any chat LLM in 60 seconds.
- **CTOs & B2B Engineering Leaders:** Need similar `Rust`/`Go`/`Nix`/Security/FinOps rigor applied to your infrastructure? Scope a fixed-price **72-Hour `VRTTI` Architecture Sprint** at **[vrtti.rasaxmachina.com](https://vrtti.rasaxmachina.com)**.
- **Investors, Researchers, Press & Executive Hiring Committees:** Inspect our cryptographic release proofs across **[`@Tianxia-Core`](https://github.com/Tianxia-Core)**, press assets at **[rasaxmachina.com/press](https://rasaxmachina.com/press)**, and founder dossier at **[rasaxmachina.com/collins](https://rasaxmachina.com/collins)**.

<!-- BUILD_LAW_SHOWCASE_END -->

---
## Polyglot Function-Feature Pair (FFP) Architecture

| Component | FFP ID & Class | Language | Role |
| :--- | :--- | :--- | :--- |
| `crates/aether_mesh` | `FFP-AETHER-MESH` (`concurrent-daemon`) | **Rust** | Hybrid TCP + Linux `AF_VSOCK` (`:4242`) mobile swarm node with bounded kill-switches and FNV-1a frame digests |
| `crates/avf_core` | `FFP-AVF-CORE` (`concurrent-daemon`) | **Rust** | Protected-VM KSM/ZRAM manifold inspector & mission orchestrator |
| `go/cmd/tx-mobile-bridge` | `FFP-MOBILE-BRIDGE` (`io-edge`) | **Go** (`CGO_ENABLED=0`) | Cross-platform desktop/relay bridge (`ping`, `telemetry`, `dispatch`, `self-test`) with HMAC-SHA256 envelope signing |
| `src/main/java/.../MainActivity.java` | `FFP-MOBILE-HUD` (`frontend`) | **Java (Android SDK 34)** | Dark-canvas (`#080808`) telemetry cockpit with pause-aware UI lifecycle |
| `src/main/java/.../SovereignHypervisorService.java` | `FFP-MOBILE-HYPERVISOR` (`concurrent-daemon`) | **Java (Android SDK 34)** | Loopback-isolated (`127.0.0.1:4343`) service with 256 KiB bounded telemetry rotation |
| `native/tianxia_payload.cpp` | `FFP-AVF-PAYLOAD` (`edge-agent`) | **C++** | Bounded `AF_VSOCK` guest payload entrypoint |

---

## Signed Release Artifacts (`v2.0.0`)

- `tianxia-mobile-v2.0.0-android-arm64.apk` — Signed with Android APK Signature Scheme v2/v3 (`apksigner`) + Ed25519 `.sig`
- `aether-mesh-v2.0.0-android-aarch64` — Stripped Rust `aarch64-linux-android` daemon
- `tx-mobile-bridge-v2.0.0-linux-x86_64` — Static Linux `x86_64` host bridge
- `tx-mobile-bridge-v2.0.0-linux-aarch64` — Static Linux `aarch64` host bridge
- `tx-mobile-bridge-v2.0.0-windows-x86_64.exe` — Static Windows `x86_64` host bridge
- `tx-mobile-bridge-v2.0.0-android-aarch64` — Native Android `aarch64` host bridge

### Cryptographic Verification

```bash
sha256sum -c SHA256SUMS
openssl pkeyutl -verify -pubin -inkey keys/RELEASE_SIGNING_KEY.pub.pem -rawin -in SHA256SUMS -sigfile SHA256SUMS.sig
apksigner verify --verbose tianxia-mobile-v2.0.0-android-arm64.apk
```

---

## Building From Source

```bash
# 1. Rust workspace tests & ARM64 Android build
cargo test --workspace
cargo build --release --target aarch64-linux-android -p aether_mesh

# 2. Go mobile bridge tests & static build
(cd go && go test -v ./... && CGO_ENABLED=0 go build -trimpath -ldflags="-s -w" ./cmd/tx-mobile-bridge)

# 3. Android APK assembly
gradle assembleRelease
```

---

## Governance & Entity Status

Maintained by **RASA-X-MACHINA** *(Proposed Estonian Deep-Tech Entity — Startup Committee application in preparation)* · [`rasaxmachina.com`](https://rasaxmachina.com). Licensed under [Apache-2.0](LICENSE).
