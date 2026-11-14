// ffp: FFP-MOBILE-HUD frontend java
package com.rasaxmachina.tianxia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Sovereign Mobile Client & AetherMesh Node Cockpit (v2.0.0).
 * Adheres to Tripura Sundari dark-canvas geometry (#080808) and Bagalamukhi
 * bounded UI refresh lifecycle (zero background polling when paused).
 */
public final class MainActivity extends Activity implements Runnable, View.OnClickListener {
    private static final long REFRESH_MS = 2000L;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private TextView statusBadge;
    private TextView telemetryView;
    private File telemetryFile;
    private boolean active = false;

    static final class StatusUpdater implements Runnable {
        private final TextView badge;
        private final String message;

        StatusUpdater(TextView badge, String message) {
            this.badge = badge;
            this.message = message;
        }

        @Override
        public void run() {
            if (badge != null) {
                badge.setText(message);
            }
        }
    }

    static final class PingWorker implements Runnable {
        private final Handler handler;
        private final TextView badge;

        PingWorker(Handler handler, TextView badge) {
            this.handler = handler;
            this.badge = badge;
        }

        @Override
        public void run() {
            long t0 = System.nanoTime();
            String result;
            try (Socket sock = new Socket()) {
                sock.connect(new InetSocketAddress("127.0.0.1", SovereignHypervisorService.BRIDGE_PORT), 1500);
                sock.setSoTimeout(1500);
                OutputStream out = sock.getOutputStream();
                out.write("PING\n".getBytes(StandardCharsets.UTF_8));
                out.flush();
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8)
                );
                String line = rd.readLine();
                double rttMs = (System.nanoTime() - t0) / 1_000_000.0;
                result = String.format("PING OK (%.2f ms): %s", rttMs, line != null ? line : "ACK");
            } catch (Exception exc) {
                result = "PING ERROR: " + exc.getClass().getSimpleName();
            }
            handler.post(new StatusUpdater(badge, result));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, SovereignHypervisorService.class));

        File poolDir = new File(getFilesDir(), "TianxiaPool");
        telemetryFile = new File(poolDir, "aether_mesh_telemetry.jsonl");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#080808"));
        root.setPadding(40, 48, 40, 40);

        TextView title = new TextView(this);
        title.setText("TIANXIA MOBILE · AETHERMESH v2.0.0");
        title.setTextColor(Color.parseColor("#00FFCC"));
        title.setTextSize(18f);
        title.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        root.addView(title);

        statusBadge = new TextView(this);
        statusBadge.setText("NODE STATE: LOCAL LOOPBACK LATCHED (127.0.0.1:4343)");
        statusBadge.setTextColor(Color.parseColor("#A6ADC8"));
        statusBadge.setTextSize(12f);
        statusBadge.setPadding(0, 12, 0, 24);
        statusBadge.setTypeface(Typeface.MONOSPACE);
        root.addView(statusBadge);

        Button probeButton = new Button(this);
        probeButton.setText("RUN DETERMINISTIC MESH PING");
        probeButton.setBackgroundColor(Color.parseColor("#181825"));
        probeButton.setTextColor(Color.WHITE);
        probeButton.setOnClickListener(this);
        root.addView(probeButton);

        ScrollView scroller = new ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        );
        scrollParams.topMargin = 24;
        scroller.setLayoutParams(scrollParams);
        scroller.setBackgroundColor(Color.parseColor("#0E0E14"));

        telemetryView = new TextView(this);
        telemetryView.setTextColor(Color.parseColor("#CDD6F4"));
        telemetryView.setTextSize(11f);
        telemetryView.setPadding(24, 24, 24, 24);
        telemetryView.setGravity(Gravity.TOP | Gravity.START);
        telemetryView.setTypeface(Typeface.MONOSPACE);
        telemetryView.setText("Initializing AetherMesh telemetry ring...");
        scroller.addView(telemetryView);

        root.addView(scroller);
        setContentView(root);
    }

    @Override
    public void run() {
        if (!active) {
            return;
        }
        renderTelemetryTail();
        uiHandler.postDelayed(this, REFRESH_MS);
    }

    @Override
    public void onClick(View v) {
        triggerLoopbackPing();
    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
        uiHandler.post(this);
    }

    @Override
    protected void onPause() {
        active = false;
        uiHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    private void triggerLoopbackPing() {
        new Thread(new PingWorker(uiHandler, statusBadge), "AetherMesh-UI-Ping").start();
    }

    private void renderTelemetryTail() {
        if (telemetryFile == null || !telemetryFile.exists()) {
            return;
        }
        Deque<String> ring = new ArrayDeque<>(32);
        try (BufferedReader br = new BufferedReader(new FileReader(telemetryFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (ring.size() >= 30) {
                    ring.removeFirst();
                }
                ring.addLast(line);
            }
            if (!ring.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String item : ring) {
                    sb.append(item).append('\n');
                }
                telemetryView.setText(sb.toString());
            }
        } catch (Exception ignored) {
            // Non-fatal concurrent rotation read
        }
    }
}
