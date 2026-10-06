package com.momo.mousehome;

import java.io.DataOutputStream;

public class RootRunner {
    private static void root(String commands) throws Exception {
        Process p = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(p.getOutputStream());
        os.writeBytes(commands);
        if (!commands.endsWith("\n")) os.writeBytes("\n");
        os.writeBytes("exit\n");
        os.flush();
        p.waitFor();
    }


    public static boolean isDaemonRunning() {
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(p.getInputStream()));
            os.writeBytes("if [ -f /data/local/tmp/mousehome_v3_8.pid ] && kill -0 $(cat /data/local/tmp/mousehome_v3_8.pid) 2>/dev/null; then echo RUNNING; else echo STOPPED; fi\n");
            os.writeBytes("exit\n");
            os.flush();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("RUNNING")) {
                    p.waitFor();
                    return true;
                }
            }
            p.waitFor();
        } catch (Exception ignored) {}
        return false;
    }

    public static void startDaemon() throws Exception {
        String script =
            "#!/system/bin/sh\n" +
            "rm -f /data/local/tmp/middle_down\n" +
            "getevent -l /dev/input/event2 | while read line; do\n" +
            " case \"$line\" in\n" +
            "  *BTN_MIDDLE*DOWN*) touch /data/local/tmp/middle_down; (sleep 1; if [ -f /data/local/tmp/middle_down ]; then input keyevent KEYCODE_HOME; rm -f /data/local/tmp/middle_down; fi) & ;;\n" +
            "  *BTN_MIDDLE*UP*) rm -f /data/local/tmp/middle_down ;;\n" +
            " esac\n" +
            "done\n";

        String escaped = script.replace("'", "'\\''");
        root(
            "if [ -f /data/local/tmp/mousehome_v3_8.pid ]; then kill $(cat /data/local/tmp/mousehome_v3_8.pid) 2>/dev/null; fi\n" +
            "pkill -f 'getevent -l /dev/input/event2' 2>/dev/null\n" +
            "printf '%s' '" + escaped + "' > /data/local/tmp/mousehome_v3_8.sh\n" +
            "chmod 755 /data/local/tmp/mousehome_v3_8.sh\n" +
            "nohup /system/bin/sh /data/local/tmp/mousehome_v3_8.sh >/data/local/tmp/mousehome_v3_8.log 2>&1 & echo $! >/data/local/tmp/mousehome_v3_8.pid\n"
        );
    }

    public static void stopDaemon() throws Exception {
        root(
            "if [ -f /data/local/tmp/mousehome_v3_8.pid ]; then kill $(cat /data/local/tmp/mousehome_v3_8.pid) 2>/dev/null; rm -f /data/local/tmp/mousehome_v3_8.pid; fi\n" +
            "pkill -f 'getevent -l /dev/input/event2' 2>/dev/null\n" +
            "rm -f /data/local/tmp/middle_down\n"
        );
    }
}
