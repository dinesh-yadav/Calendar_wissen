package com.example.calendarwidget.util;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AutoStartManager {
    private static final String BATCH_FILE = "run.bat";
    private static final String STARTUP_FILE = "CalendarWidget.bat";

    public static void enableAutoStart(JFrame parentFrame) {
        try {
            Path source = Paths.get(BATCH_FILE);
            Path target = Paths.get(System.getenv("APPDATA"), "Microsoft", "Windows", "Start Menu", "Programs", "Startup", STARTUP_FILE);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            JOptionPane.showMessageDialog(parentFrame, "Auto-start enabled. The app will start with Windows.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parentFrame, "Failed to enable auto-start: " + ex.getMessage());
        }
    }

    public static void disableAutoStart(JFrame parentFrame) {
        try {
            Path target = Paths.get(System.getenv("APPDATA"), "Microsoft", "Windows", "Start Menu", "Programs", "Startup", STARTUP_FILE);
            Files.deleteIfExists(target);
            JOptionPane.showMessageDialog(parentFrame, "Auto-start disabled.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parentFrame, "Failed to disable auto-start: " + ex.getMessage());
        }
    }
}