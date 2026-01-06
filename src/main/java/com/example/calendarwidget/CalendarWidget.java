package com.example.calendarwidget;

import com.example.calendarwidget.controller.CalendarController;
import com.example.calendarwidget.model.Holiday;
import com.example.calendarwidget.service.HolidayService;
import com.example.calendarwidget.util.AutoStartManager;
import com.example.calendarwidget.view.CalendarView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalendarWidget {
    private static final Logger logger = Logger.getLogger(CalendarWidget.class.getName());
    private static JFrame calendarFrame;
    private static CalendarView calendarView;
    private static CalendarController calendarController;
    private static Map<LocalDate, List<Holiday>> holidays;

    public static void main(String[] args) {
        // Configure logging
        try {
            java.util.logging.LogManager.getLogManager().readConfiguration(
                CalendarWidget.class.getClassLoader().getResourceAsStream("logging.properties")
            );
        } catch (Exception e) {
            System.err.println("Failed to load logging configuration: " + e.getMessage());
        }

        logger.info("Starting Calendar Widget application");

        try {
            HolidayService holidayService = new HolidayService();
            holidays = holidayService.loadHolidays();
            logger.info("Holiday data loaded successfully");

            if (!SystemTray.isSupported()) {
                logger.severe("System tray not supported on this platform");
                return;
            }

            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("icon.png");
            logger.fine("System tray icon loaded");

            TrayIcon trayIcon = new TrayIcon(image, "Calendar Widget");
            trayIcon.setImageAutoSize(true);

            PopupMenu menu = new PopupMenu();
            MenuItem showItem = new MenuItem("Show Calendar");
            showItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    logger.fine("Show Calendar menu item clicked");
                    showCalendar();
                }
            });
            menu.add(showItem);

            MenuItem autoStartItem = new MenuItem("Enable Auto-start");
            autoStartItem.addActionListener(e -> {
                logger.info("Enable Auto-start menu item clicked");
                AutoStartManager.enableAutoStart(calendarFrame);
            });
            menu.add(autoStartItem);

            MenuItem disableItem = new MenuItem("Disable Auto-start");
            disableItem.addActionListener(e -> {
                logger.info("Disable Auto-start menu item clicked");
                AutoStartManager.disableAutoStart(calendarFrame);
            });
            menu.add(disableItem);

            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    logger.info("Exit menu item clicked, shutting down application");
                    System.exit(0);
                }
            });
            menu.add(exitItem);

            trayIcon.setPopupMenu(menu);

            try {
                tray.add(trayIcon);
                logger.info("Tray icon added to system tray successfully");
            } catch (AWTException e) {
                logger.log(Level.SEVERE, "TrayIcon could not be added to system tray", e);
                return;
            }

            // Create calendar frame
            logger.fine("Creating calendar frame");
            calendarFrame = new JFrame("Calendar Widget");
            calendarFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            calendarFrame.setSize(800, 400);

            JPanel mainPanel = new JPanel(new BorderLayout());

            JPanel buttonPanel = new JPanel();
            JButton prevButton = new JButton("< Previous");
            JButton nextButton = new JButton("Next >");
            buttonPanel.add(prevButton);
            buttonPanel.add(nextButton);

            mainPanel.add(buttonPanel, BorderLayout.NORTH);

            calendarView = new CalendarView(holidays);
            mainPanel.add(calendarView, BorderLayout.CENTER);

            calendarFrame.add(mainPanel);

            calendarController = new CalendarController(calendarView, holidays);

            prevButton.addActionListener(e -> {
                logger.fine("Previous button clicked");
                calendarController.navigatePrevious();
            });
            nextButton.addActionListener(e -> {
                logger.fine("Next button clicked");
                calendarController.navigateNext();
            });

            logger.info("Calendar Widget application initialized successfully");
            showCalendar();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during application startup", e);
        }
    }

    private static void showCalendar() {
        logger.fine("Showing calendar window");
        calendarFrame.setVisible(true);
        calendarFrame.toFront();
    }
}