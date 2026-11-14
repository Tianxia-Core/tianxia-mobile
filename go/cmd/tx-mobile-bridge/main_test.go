// ffp: FFP-MOBILE-BRIDGE-TEST io-edge go
package main

import (
	"bufio"
	"net"
	"strings"
	"testing"
	"time"
)

func TestEnvelopeSignAndVerify(t *testing.T) {
	env := BridgeEnvelope{Op: "dispatch", Payload: "mesh-sync", Timestamp: 1700000000}
	SignEnvelope(&env, "unit-test-secret")
	if env.HMAC == "" {
		t.Fatal("expected non-empty HMAC")
	}
	if !VerifyEnvelope(env, "unit-test-secret") {
		t.Fatal("expected valid HMAC verification")
	}
	if VerifyEnvelope(env, "wrong-secret") {
		t.Fatal("expected wrong secret to fail verification")
	}
}

func TestRelayAndSelfTest(t *testing.T) {
	if err := runSelfTest(); err != nil {
		t.Fatalf("runSelfTest failed: %v", err)
	}

	// Mock upstream AetherMesh node
	up, err := net.Listen("tcp", "127.0.0.1:0")
	if err != nil {
		t.Fatal(err)
	}
	defer up.Close()
	go func() {
		c, err := up.Accept()
		if err != nil {
			return
		}
		defer c.Close()
		line, _ := bufio.NewReader(c).ReadString('\n')
		if strings.Contains(line, "TELEMETRY") {
			_, _ = c.Write([]byte("{\"node\":\"aether_mesh\",\"status\":\"ONLINE\"}\n"))
		}
	}()

	reply, err := ExchangeNodeCommand(up.Addr().String(), "TELEMETRY", 2*time.Second)
	if err != nil {
		t.Fatalf("ExchangeNodeCommand failed: %v", err)
	}
	if !strings.Contains(reply, "\"ONLINE\"") {
		t.Fatalf("unexpected reply: %s", reply)
	}
}
