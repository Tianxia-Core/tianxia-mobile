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

## 🏛️ Governed by [`Build Law`](https://github.com/Tianxia-Core/Build-Law) — Clean-Room Public Edition & Stakeholder Pathways

This repository ships as a **`PUBLIC_CLEANROOM_SEED` Edition** under **[`Build Law`](https://github.com/Tianxia-Core/Build-Law)**—containing zero pre-populated personal data, zero embedded secrets, and deterministic `init-clean` bootstrapping (`nimbus2 init-clean` / `build-law init-clean`) so every adopter starts with a fresh, sovereign workspace of their own:
- **Developers & AI Builders:** Combine **[`FC-Estimator`](https://github.com/Tianxia-Core/FC-Estimator)** (`95%` accurate pre-build sizing), **[`Build Law`](https://github.com/Tianxia-Core/Build-Law)** (`5.0×–8.8×` token efficiency across 20+ IDEs/agents), and **[`nimbus²`](https://github.com/Tianxia-Core/nimbus2)** (`nimbus2 ship` + `$0.00` 14-Gate PR review).
- **CTOs & B2B Engineering Leaders:** Need similar `Rust`/`Go`/`Nix`/Security/FinOps rigor applied to your infrastructure? Scope a fixed-price **72-Hour `VRTTI` Architecture Sprint** at **[vrtti.rasaxmachina.com](https://vrtti.rasaxmachina.com)**.
- **Investors, Researchers, Press & Executive Hiring Committees:** Inspect cryptographic release proofs across **[`@Tianxia-Core`](https://github.com/Tianxia-Core)**, press assets in **[`@Tianxia-Presence/brand-kit`](https://github.com/Tianxia-Presence/brand-kit)**, commercial suites in **[`@Tianxia-Revenues`](https://github.com/Tianxia-Revenues)**, and founder dossier at **[rasaxmachina.com/collins](https://rasaxmachina.com/collins)**.

### 🔗 Composable Sovereign Stack — Works Natively With (`@Tianxia-Core` · `@Tianxia-Presence` · `@Tianxia-Revenues`)

Every tool in the **RASA-X-MACHINA** ecosystem is standalone (`0` lock-in bloat), yet designed to compose seamlessly across pre-build sizing, IDE governance, polyglot compilation, security custody, and autonomous operations:

| Lifecycle Layer | Companion Tool(s) | How They Work Together |
| :--- | :--- | :--- |
| **Pre-Build Sizing & FinOps** | **[`FC-Estimator`](https://github.com/Tianxia-Core/FC-Estimator)** (`MIT`) | Sizes `ceil(FC / 8)` microservice bounds, active build minutes (`95.0%` accuracy) & `8.8×` EUR token savings before coding. |
| **Constitutional Governance** | **[`Build Law`](https://github.com/Tianxia-Core/Build-Law)** (`MIT`) | Embeds the 4-Pillar, 14-Gate rules & `< 5 ms` `$0.00` static verifier across Claude, Cursor, VS Code, OpenCode & MCP. |
| **Executive Build, PR & Proxy Manager** | **[`nimbus²`](https://github.com/Tianxia-Core/nimbus2)** (`MPL-2.0`) & **[`GitGate`](https://github.com/Tianxia-Core/GitGate)** (`MIT`) | Runs `gitgate` / `nimbus2 ship`, zero-token 14-Gate `review-diff`, `init-clean` zero-PII seeding & high-concurrency TLS 1.3 proxy routing. |
| **Polyglot Wire Contracts** | **[`Jivascriptocle`](https://github.com/Tianxia-Core/Jivascriptocle)** (`Apache-2.0`) | Compiles single `.jiva` contracts into synchronized `Rust`, `Zig`, `OCaml`, `TypeScript`, `Nix` & compact `TOON` schemas. |
| **DevSecOps & Secret Enclave** | **[`AegisScan`](https://github.com/Tianxia-Core/AegisScan)** & **[`AetherVault`](https://github.com/Tianxia-Core/AetherVault)** (`MIT`) | 8-domain static supply-chain/prompt-injection scanner paired with `AES-256-GCM` `chmod 0600` runtime secret custody. |
| **Sovereign OS, Firewall & Mesh** | **[`TianxiaOS`](https://github.com/Tianxia-Core/TianxiaOS)** · **[`Faery-Fortress`](https://github.com/Tianxia-Core/Faery-Fortress)** · **[`tianxia-mobile`](https://github.com/Tianxia-Core/tianxia-mobile)** | Declarative NixOS operating substrate, `< 4 µs` `AF_XDP` + Rosenpass post-quantum firewall & signed Android ARM64 mesh node. |
| **Intelligence, Clearing, Ledger & Logistics** | **[`Ndimmorwyn`](https://github.com/Tianxia-Core/Ndimmorwyn)** · **[`AlatyrAtal`](https://github.com/Tianxia-Core/AlatyrAtal)** · **[`Tetraledger`](https://github.com/Tianxia-Core/Tetraledger)** · **[`MURUA`](https://github.com/Tianxia-Core/MURUA)** · **[`Scour`](https://github.com/Tianxia-Core/Scour)** | Constructal `-79.6%` router + `EjAwen` CRM, 4-Stratum `EFEMERA→ETAMANGA` clearinghouse, 4-way `x402` ledger, postal telemetry & signal radar. |
| **Press Assets & B2B Sprints** | **[`@Tianxia-Presence/brand-kit`](https://github.com/Tianxia-Presence/brand-kit)** & **[`@Tianxia-Revenues`](https://github.com/Tianxia-Revenues)** | Official vector identity/design tokens (`WCAG 2.2 AA`) and fixed-scope **72h `VRTTI` Architecture Sprints** at [rasaxmachina.com](https://rasaxmachina.com). |

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
