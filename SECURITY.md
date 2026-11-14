# Security Policy

## Supported Versions

| Version | Supported |
| :--- | :--- |
| `2.0.x` | Yes |
| `< 2.0.0` | No |

## Mobile Security Posture

- **Loopback Isolation**: `SovereignHypervisorService` binds strictly to `127.0.0.1:4343` (`exported="false"`), preventing LAN exposure on untrusted networks.
- **Authenticated Bridge Dispatch**: `tx-mobile-bridge` supports HMAC-SHA256 envelope signing (`TX_MOBILE_HMAC_KEY`) with bounded read/write deadlines.
- **Dual-Layer Artifact Signing**: Release APKs carry Android APK Signature Scheme v2/v3 signatures plus detached Ed25519 release signatures (`keys/RELEASE_SIGNING_KEY.pub.pem`).

## Reporting a Vulnerability

Report suspected security vulnerabilities privately to [Encrypted Security Dispatch](https://rasaxmachina.com/collins#encrypted-dispatch). Do not disclose unpatched vulnerabilities in public issues.
