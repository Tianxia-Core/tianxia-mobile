<p align="center">
  <img src="./brand-kit/01-logos/lockups-and-social/dual-lockup-rxm-x-tianxiaos.svg" alt="RASA-X-MACHINA · tianxiaOS" width="680"/>
</p>

# tianxia-mobile

**Sovereign Mobile Client, AetherMesh Node Daemon & Cross-Platform Host Bridge**

`tianxia-mobile` unifies the Android `arm64-v8a` mobile client (`com.rasaxmachina.tianxia`), the Rust `aether_mesh` (`aarch64-linux-android`) and `avf_nomad_core` edge daemons, and the static Go `tx-mobile-bridge` host relay across Linux, Windows, and Android.

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
