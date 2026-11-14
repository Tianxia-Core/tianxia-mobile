// ffp: FFP-MOBILE-BRIDGE io-edge go
package main

import (
	"bufio"
	"crypto/hmac"
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"errors"
	"flag"
	"fmt"
	"net"
	"os"
	"strings"
	"time"
)

const (
	Version         = "2.1.0"
	DefaultEndpoint = "127.0.0.1:4242"
)

type BridgeEnvelope struct {
	Op        string `json:"op"`
	Payload   string `json:"payload,omitempty"`
	Timestamp int64  `json:"ts"`
	HMAC      string `json:"hmac,omitempty"`
}

func SignEnvelope(env *BridgeEnvelope, secret string) {
	if secret == "" {
		return
	}
	mac := hmac.New(sha256.New, []byte(secret))
	fmt.Fprintf(mac, "%s|%s|%d", env.Op, env.Payload, env.Timestamp)
	env.HMAC = hex.EncodeToString(mac.Sum(nil))
}

func VerifyEnvelope(env BridgeEnvelope, secret string) bool {
	if secret == "" {
		return true
	}
	mac := hmac.New(sha256.New, []byte(secret))
	fmt.Fprintf(mac, "%s|%s|%d", env.Op, env.Payload, env.Timestamp)
	expected := hex.EncodeToString(mac.Sum(nil))
	return hmac.Equal([]byte(expected), []byte(env.HMAC))
}

func ExchangeNodeCommand(addr, rawLine string, timeout time.Duration) (string, error) {
	conn, err := net.DialTimeout("tcp", addr, timeout)
	if err != nil {
		return "", err
	}
	defer conn.Close()
	_ = conn.SetDeadline(time.Now().Add(timeout))

	if !strings.HasSuffix(rawLine, "\n") {
		rawLine += "\n"
	}
	if _, err := conn.Write([]byte(rawLine)); err != nil {
		return "", err
	}
	rd := bufio.NewReader(conn)
	reply, err := rd.ReadString('\n')
	if err != nil && reply == "" {
		return "", err
	}
	return strings.TrimSpace(reply), nil
}

func RunBridgeRelayOnce(listenAddr, upstreamAddr string, timeout time.Duration) error {
	ln, err := net.Listen("tcp", listenAddr)
	if err != nil {
		return err
	}
	defer ln.Close()

	if tcpLn, ok := ln.(*net.TCPListener); ok {
		_ = tcpLn.SetDeadline(time.Now().Add(timeout))
	}
	client, err := ln.Accept()
	if err != nil {
		return err
	}
	defer client.Close()
	_ = client.SetDeadline(time.Now().Add(timeout))

	line, err := bufio.NewReader(client).ReadString('\n')
	if err != nil && line == "" {
		return err
	}
	reply, err := ExchangeNodeCommand(upstreamAddr, line, timeout)
	if err != nil {
		return err
	}
	_, err = client.Write([]byte(reply + "\n"))
	return err
}

func resolveEndpoint(explicit string) string {
	if explicit != "" {
		return explicit
	}
	if env := os.Getenv("TX_MOBILE_NODE_ADDR"); env != "" {
		return env
	}
	return DefaultEndpoint
}

func usage() {
	fmt.Fprintf(os.Stderr, `tx-mobile-bridge v%s — Sovereign Mobile Node & AetherMesh Host Bridge

Usage:
  tx-mobile-bridge <command> [flags]

Commands:
  ping        Ping an AetherMesh mobile node and measure RTT
  telemetry   Fetch live telemetry JSON from an AetherMesh node
  dispatch    Dispatch an authenticated workload command (-task <name>)
  self-test   Run local loopback AetherMesh protocol & HMAC verification
  version     Print build metadata
`, Version)
}

func runSelfTest() error {
	ln, err := net.Listen("tcp", "127.0.0.1:0")
	if err != nil {
		return err
	}
	addr := ln.Addr().String()
	done := make(chan error, 1)
	go func() {
		defer ln.Close()
		c, acceptErr := ln.Accept()
		if acceptErr != nil {
			done <- acceptErr
			return
		}
		defer c.Close()
		line, _ := bufio.NewReader(c).ReadString('\n')
		var env BridgeEnvelope
		if uErr := json.Unmarshal([]byte(strings.TrimSpace(line)), &env); uErr != nil {
			done <- uErr
			return
		}
		if !VerifyEnvelope(env, "loopback-key") {
			done <- errors.New("HMAC verification failed")
			return
		}
		_, wErr := c.Write([]byte("{\"node\":\"aether_mesh\",\"status\":\"PONG\"}\n"))
		done <- wErr
	}()

	env := BridgeEnvelope{Op: "ping", Payload: "probe", Timestamp: time.Now().Unix()}
	SignEnvelope(&env, "loopback-key")
	raw, _ := json.Marshal(env)
	reply, err := ExchangeNodeCommand(addr, string(raw), 3*time.Second)
	if err != nil {
		return err
	}
	if !strings.Contains(reply, "\"PONG\"") {
		return fmt.Errorf("unexpected reply: %s", reply)
	}
	return <-done
}

func main() {
	if len(os.Args) < 2 {
		usage()
		os.Exit(2)
	}
	sub := os.Args[1]
	if sub == "-h" || sub == "--help" || sub == "help" {
		usage()
		return
	}
	if sub == "version" || sub == "--version" || sub == "-v" {
		b, _ := json.MarshalIndent(map[string]any{
			"name":     "tx-mobile-bridge",
			"version":  Version,
			"protocol": "AetherMesh-v2 (TCP / ADB-Forward / AF_VSOCK)",
			"cgo":      false,
		}, "", "  ")
		fmt.Println(string(b))
		return
	}
	if sub == "self-test" {
		if err := runSelfTest(); err != nil {
			fmt.Fprintf(os.Stderr, "SELF-TEST FAILED: %v\n", err)
			os.Exit(1)
		}
		fmt.Println("{\"self_test\":\"PASS\",\"version\":\"" + Version + "\"}")
		return
	}

	fs := flag.NewFlagSet(sub, flag.ExitOnError)
	addrFlag := fs.String("addr", "", "Mobile node endpoint (default: 127.0.0.1:4242 or $TX_MOBILE_NODE_ADDR)")
	taskFlag := fs.String("task", "health_sync", "Workload task name for dispatch")
	timeoutFlag := fs.Duration("timeout", 4*time.Second, "Network I/O timeout")
	_ = fs.Parse(os.Args[2:])

	endpoint := resolveEndpoint(*addrFlag)
	secret := os.Getenv("TX_MOBILE_HMAC_KEY")

	switch sub {
	case "ping":
		t0 := time.Now()
		env := BridgeEnvelope{Op: "ping", Timestamp: t0.Unix()}
		SignEnvelope(&env, secret)
		raw, _ := json.Marshal(env)
		reply, err := ExchangeNodeCommand(endpoint, string(raw), *timeoutFlag)
		if err != nil {
			fmt.Fprintf(os.Stderr, "ERROR: %v\n", err)
			os.Exit(1)
		}
		rttMs := float64(time.Since(t0).Microseconds()) / 1000.0
		out, _ := json.MarshalIndent(map[string]any{
			"endpoint": endpoint,
			"rtt_ms":   rttMs,
			"node":     json.RawMessage(reply),
		}, "", "  ")
		fmt.Println(string(out))
	case "telemetry":
		reply, err := ExchangeNodeCommand(endpoint, "TELEMETRY", *timeoutFlag)
		if err != nil {
			fmt.Fprintf(os.Stderr, "ERROR: %v\n", err)
			os.Exit(1)
		}
		fmt.Println(reply)
	case "dispatch":
		env := BridgeEnvelope{Op: "dispatch", Payload: *taskFlag, Timestamp: time.Now().Unix()}
		SignEnvelope(&env, secret)
		raw, _ := json.Marshal(env)
		reply, err := ExchangeNodeCommand(endpoint, string(raw), *timeoutFlag)
		if err != nil {
			fmt.Fprintf(os.Stderr, "ERROR: %v\n", err)
			os.Exit(1)
		}
		fmt.Println(reply)
	default:
		fmt.Fprintf(os.Stderr, "ERROR: unknown command %q\n", sub)
		usage()
		os.Exit(2)
	}
}
