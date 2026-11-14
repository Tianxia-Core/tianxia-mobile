# Contributing to Tianxia Mobile

1. **Zero Build Cache Commits**: Never commit `target/`, `build/`, `.gradle/`, `.apk`, `.so`, or prebuilt binaries to git.
2. **FFP Headers & Bounded Loops**: Every source file must carry its `# ffp:` or `// ffp:` header and enforce explicit loop/timeout bounds (`Bagalamukhi` discipline).
3. **Pre-PR Verification**:
   ```bash
   cargo test --workspace
   (cd go && go test -race -v ./...)
   gradle assembleRelease
   ```
