package com.momo.mousehome;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.FileReader;

public class MainActivity extends Activity {
    private static final String PREFS = "mousehome_prefs";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_BOOT_ID = "last_boot_id";
    private TextView status;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(24, 24, 24, 24);

        TextView t = new TextView(this);
        t.setText("MouseHome v3.8\n\nLong middle press (1 second) = HOME\nShort middle click remains unchanged.");
        t.setTextSize(22);
        box.addView(t);

        status = new TextView(this);
        status.setTextSize(22);
        box.addView(status);

        Button start = new Button(this);
        start.setText("START MOUSEHOME");
        box.addView(start);

        Button stop = new Button(this);
        stop.setText("STOP MOUSEHOME");
        box.addView(stop);

        setContentView(box);

        // Show status immediately; never wait for su/root probing.
        updateStatus();

        start.setOnClickListener(v -> {
            setEnabled(true);
            updateStatus();
            new Thread(() -> {
                try { RootRunner.startDaemon(); } catch (Exception ignored) {}
            }).start();
        });

        stop.setOnClickListener(v -> {
            setEnabled(false);
            updateStatus();
            new Thread(() -> {
                try { RootRunner.stopDaemon(); } catch (Exception ignored) {}
            }).start();
        });

        String bootId = getBootId();
        String lastBootId = prefs().getString(KEY_BOOT_ID, "");
        boolean firstLaunchThisBoot = bootId.length() > 0 && !bootId.equals(lastBootId);

        if (firstLaunchThisBoot) {
            prefs().edit().putString(KEY_BOOT_ID, bootId).apply();

            // Vendor Autostart launch after reboot:
            // obey saved state, then ALWAYS go HOME.
            new Thread(() -> {
                try {
                    if (isEnabled()) {
                        RootRunner.startDaemon();
                    } else {
                        RootRunner.stopDaemon();
                    }
                    Thread.sleep(700);
                } catch (Exception ignored) {}
                goHome();
            }).start();
        }
        // Any later/manual launch in the same boot stays on this screen.
    }

    private SharedPreferences prefs() {
        return getSharedPreferences(PREFS, MODE_PRIVATE);
    }

    private boolean isEnabled() {
        return prefs().getBoolean(KEY_ENABLED, true);
    }

    private void setEnabled(boolean enabled) {
        prefs().edit().putBoolean(KEY_ENABLED, enabled).apply();
    }

    private void updateStatus() {
        boolean enabled = isEnabled();
        status.setText(
            "\nSaved setting: " + (enabled ? "START / ENABLED" : "STOP / DISABLED") +
            "\nMouseHome status: " + (enabled ? "RUNNING" : "STOPPED") + "\n"
        );
    }

    private String getBootId() {
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/sys/kernel/random/boot_id"))) {
            String s = br.readLine();
            return s == null ? "" : s.trim();
        } catch (Exception e) {
            return "";
        }
    }

    private void goHome() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(home);
    }
}
