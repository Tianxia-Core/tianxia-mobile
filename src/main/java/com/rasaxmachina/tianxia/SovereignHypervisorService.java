// ffp: FFP-MOBILE-HYPERVISOR concurrent-daemon java
package com.rasaxmachina.tianxia;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.json.JSONObject;

/**
 * SovereignHypervisorService v2.0.0 — Loopback-bound (127.0.0.1:4343)
 * AetherMesh & AVF telemetry broker with bounded file eviction (Kali)
 * and deterministic shutdown discipline (Bagalamukhi).
 */
public final class SovereignHypervisorService extends Service {
    public static final int BRIDGE_PORT = 4343;
    static final long MAX_LOG_BYTES = 262_144L; // 256 KiB bounded telemetry ring

    final AtomicBoolean running = new AtomicBoolean(false);
    final AtomicLong processedFrames = new AtomicLong(0L);
    volatile ServerSocket serverSocket;

    static final class BridgeClientWorker implements Runnable {
        private final SovereignHypervisorService svc;
        private final Socket socket;

        BridgeClientWorker(SovereignHypervisorService svc, Socket socket) {
            this.svc = svc;
            this.socket = socket;
        }

        @Override
        public void run() {
            svc.handleClient(socket);
        }
    }

    static final class BridgeAcceptWorker implements Runnable {
        private final SovereignHypervisorService svc;

        BridgeAcceptWorker(SovereignHypervisorService svc) {
            this.svc = svc;
        }

        @Override
        public void run() {
            try {
                ServerSocket ss = new ServerSocket(BRIDGE_PORT, 16, InetAddress.getByName("127.0.0.1"));
                svc.serverSocket = ss;
                while (svc.running.get()) {
                    Socket client = ss.accept();
                    Thread worker = new Thread(new BridgeClientWorker(svc, client), "AVF-Client-Worker");
                    worker.setDaemon(true);
                    worker.start();
                }
            } catch (Exception ignored) {
                // Clean socket close on service teardown
            }
        }
    }

    static final class TelemetryHeartbeatWorker implements Runnable {
        private final SovereignHypervisorService svc;
        private final File filesDir;

        TelemetryHeartbeatWorker(SovereignHypervisorService svc, File filesDir) {
            this.svc = svc;
            this.filesDir = filesDir;
        }

        @Override
        public void run() {
            File poolDir = new File(filesDir, "TianxiaPool");
            if (!poolDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                poolDir.mkdirs();
            }
            File logFile = new File(poolDir, "aether_mesh_telemetry.jsonl");
            while (svc.running.get()) {
                try {
                    if (logFile.exists() && logFile.length() > MAX_LOG_BYTES) {
                        File prev = new File(poolDir, "aether_mesh_telemetry.prev.jsonl");
                        //noinspection ResultOfMethodCallIgnored
                        logFile.renameTo(prev);
                    }
                    JSONObject snap = new JSONObject();
                    snap.put("ts_ms", System.currentTimeMillis());
                    snap.put("abi", Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "arm64-v8a");
                    snap.put("sdk", Build.VERSION.SDK_INT);
                    snap.put("bridge_port", BRIDGE_PORT);
                    snap.put("frames", svc.processedFrames.get());
                    try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
                        fos.write((snap.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                    }
                } catch (Exception ignored) {
                    // Preserve service liveness on transient storage errors
                }
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (running.compareAndSet(false, true)) {
            startLoopbackBridge();
            startTelemetryHeartbeat();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void startLoopbackBridge() {
        Thread bridgeThread = new Thread(new BridgeAcceptWorker(this), "AetherMesh-Loopback-Bridge");
        bridgeThread.setDaemon(true);
        bridgeThread.start();
    }

    void handleClient(Socket socket) {
        try (Socket s = socket) {
            s.setSoTimeout(3000);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8)
            );
            OutputStream output = s.getOutputStream();
            String line;
            int handled = 0;
            while (running.get() && handled < 32 && (line = reader.readLine()) != null) {
                handled++;
                long seq = processedFrames.incrementAndGet();
                JSONObject reply = new JSONObject();
                reply.put("service", "SovereignHypervisorService");
                reply.put("version", "2.0.0");
                reply.put("seq", seq);
                reply.put("status", line.trim().equalsIgnoreCase("PING") ? "PONG" : "ACK");
                output.write((reply.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                output.flush();
            }
        } catch (Exception ignored) {
            // Bounded timeout or peer disconnect
        }
    }

    private void startTelemetryHeartbeat() {
        Thread telemetryThread = new Thread(
                new TelemetryHeartbeatWorker(this, getFilesDir()),
                "AetherMesh-Telemetry-Writer"
        );
        telemetryThread.setDaemon(true);
        telemetryThread.start();
    }

    @Override
    public void onDestroy() {
        running.set(false);
        try {
            ServerSocket ss = serverSocket;
            if (ss != null) {
                ss.close();
            }
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
