#!/usr/bin/env bash
# ffp: FFP-MOBILE-BOOT glue bash
# SLECO-SO++ Mobile Mesh Node Bootstrap Entrypoint
set +e
set +u

MODE="${1:---status}"
NODE_BIND="${AETHER_MESH_BIND:-127.0.0.1:4242}"
SCRIPT_DIR="${0%/*}"

cleanup() {
    local rc=$?
    if [ "$rc" -ne 0 ]; then
        printf '[boot_matrix] exited with code %d\n' "$rc" >&2
    fi
}
trap cleanup EXIT

case "$MODE" in
    --status)
        printf '{"subsystem":"tianxia-mobile","version":"2.0.0","bind":"%s"}\n' "$NODE_BIND"
        ;;
    --aether-mesh)
        if [ -x "${SCRIPT_DIR}/aether_mesh" ]; then
            "${SCRIPT_DIR}/aether_mesh" --bind "$NODE_BIND"
            exit $?
        fi
        printf '[boot_matrix] aether_mesh binary not found in %s\n' "$SCRIPT_DIR" >&2
        exit 1
        ;;
    --health)
        if [ -x "${SCRIPT_DIR}/aether_mesh" ]; then
            "${SCRIPT_DIR}/aether_mesh" --health
            exit $?
        fi
        printf '{"subsystem":"tianxia-mobile","healthy":true,"mode":"jvm-only"}\n'
        ;;
    *)
        printf 'Usage: %s [--status | --aether-mesh | --health]\n' "${0##*/}" >&2
        exit 2
        ;;
esac
