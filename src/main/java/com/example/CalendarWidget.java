package com.example;

import com.toedter.calendar.JCalendar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CalendarWidget {
    private static JFrame calendarFrame;
    private static JCalendar calendar;
    private static Map<LocalDate, String> holidays = new HashMap<>();
    private static Map<LocalDate, String> vacations = new HashMap<>();

    static {
        // Sample holidays
        holidays.put(LocalDate.of(2026, 1, 1), "New Year's Day");
        holidays.put(LocalDate.of(2026, 12, 25), "Christmas Day");
        // Add more as needed
    }

    public static void main(String[] args) {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage("icon.png");

        TrayIcon trayIcon = new TrayIcon(image, "Calendar Widget");
        trayIcon.setImageAutoSize(true);

        PopupMenu menu = new PopupMenu();
        MenuItem showItem = new MenuItem("Show Calendar");
        showItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCalendar();
            }
        });
        menu.add(showItem);

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(exitItem);

        trayIcon.setPopupMenu(menu);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        // Create calendar frame
        calendarFrame = new JFrame("Calendar Widget");
        calendarFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        calendarFrame.setSize(400, 300);

        calendar = new JCalendar();
        calendar.addPropertyChangeListener("calendar", evt -> {
            LocalDate selectedDate = LocalDate.ofInstant(calendar.getDate().toInstant(), calendar.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).getZone());
            String info = "";
            if (holidays.containsKey(selectedDate)) {
                info += "Holiday: " + holidays.get(selectedDate) + "\n";
            }
            if (vacations.containsKey(selectedDate)) {
                info += "Vacation: " + vacations.get(selectedDate) + "\n";
            }
            if (info.isEmpty()) {
                info = "No special events";
            }
            JOptionPane.showMessageDialog(calendarFrame, info);
        });

        calendarFrame.add(calendar);
        calendarFrame.setVisible(false);
    }

    private static void showCalendar() {
        calendarFrame.setVisible(true);
        calendarFrame.toFront();
    }
}