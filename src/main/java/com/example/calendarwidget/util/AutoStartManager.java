package com.example.calendarwidget.util;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoStartManager {
    private static final Logger logger = Logger.getLogger(AutoStartManager.class.getName());
    private static final String BATCH_FILE = "run.bat";
    private static final String STARTUP_FILE = "CalendarWidget.bat";

    public static void enableAutoStart(JFrame parentFrame) {
        logger.info("Attempting to enable auto-start");
        try {
            Path source = Paths.get(BATCH_FILE);
            Path target = Paths.get(System.getenv("APPDATA"), "Microsoft", "Windows", "Start Menu", "Programs", "Startup", STARTUP_FILE);

            if (!Files.exists(source)) {
                logger.warning("Batch file not found: " + source.toAbsolutePath());
                JOptionPane.showMessageDialog(parentFrame, "Batch file not found. Please ensure run.bat exists in the application directory.");
                return;
            }

            logger.fine("Copying " + source.toAbsolutePath() + " to " + target.toAbsolutePath());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Auto-start enabled successfully");
            JOptionPane.showMessageDialog(parentFrame, "Auto-start enabled. The app will start with Windows.");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to enable auto-start", ex);
            JOptionPane.showMessageDialog(parentFrame, "Failed to enable auto-start: " + ex.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Unexpected error while enabling auto-start", ex);
            JOptionPane.showMessageDialog(parentFrame, "Unexpected error while enabling auto-start: " + ex.getMessage());
        }
    }

    public static void disableAutoStart(JFrame parentFrame) {
        logger.info("Attempting to disable auto-start");
        try {
            Path target = Paths.get(System.getenv("APPDATA"), "Microsoft", "Windows", "Start Menu", "Programs", "Startup", STARTUP_FILE);

            logger.fine("Deleting startup file: " + target.toAbsolutePath());
            boolean deleted = Files.deleteIfExists(target);
            if (deleted) {
                logger.info("Auto-start disabled successfully");
                JOptionPane.showMessageDialog(parentFrame, "Auto-start disabled.");
            } else {
                logger.warning("Startup file did not exist: " + target.toAbsolutePath());
                JOptionPane.showMessageDialog(parentFrame, "Auto-start was not enabled.");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to disable auto-start", ex);
            JOptionPane.showMessageDialog(parentFrame, "Failed to disable auto-start: " + ex.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Unexpected error while disabling auto-start", ex);
            JOptionPane.showMessageDialog(parentFrame, "Unexpected error while disabling auto-start: " + ex.getMessage());
        }
    }
}