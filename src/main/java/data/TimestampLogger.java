
package data;

import java.io.*;

public class TimestampLogger {
    private File logFile;

    public TimestampLogger(File logFile) {
        this.logFile = logFile;
        if (!logFile.getParentFile().exists()) logFile.getParentFile().mkdirs();
        try {
            if (!logFile.exists()) logFile.createNewFile();
        } catch (IOException e) { }
    }

    public void logLogin(String username) {
        log(username, "LOGIN");
    }

    public void logLogout(String username) {
        log(username, "LOGOUT");
    }

    private synchronized void log(String username, String action) {
        long ts = System.currentTimeMillis();
        String binary = Long.toBinaryString(ts);
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile, true))) {
            pw.println(username + "," + action + "," + binary);
        } catch (IOException e) { e.printStackTrace(); }
    }
}