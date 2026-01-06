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

public class CalendarWidget {
    private static JFrame calendarFrame;
    private static CalendarView calendarView;
    private static CalendarController calendarController;
    private static Map<LocalDate, List<Holiday>> holidays;

    public static void main(String[] args) {
        HolidayService holidayService = new HolidayService();
        holidays = holidayService.loadHolidays();

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

        MenuItem autoStartItem = new MenuItem("Enable Auto-start");
        autoStartItem.addActionListener(e -> AutoStartManager.enableAutoStart(calendarFrame));
        menu.add(autoStartItem);

        MenuItem disableItem = new MenuItem("Disable Auto-start");
        disableItem.addActionListener(e -> AutoStartManager.disableAutoStart(calendarFrame));
        menu.add(disableItem);

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

        prevButton.addActionListener(e -> calendarController.navigatePrevious());
        nextButton.addActionListener(e -> calendarController.navigateNext());

        showCalendar();
    }

    private static void showCalendar() {
        calendarFrame.setVisible(true);
        calendarFrame.toFront();
    }
}