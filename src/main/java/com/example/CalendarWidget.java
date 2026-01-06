package com.example;

import com.toedter.calendar.JCalendar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarWidget {
    private static JFrame calendarFrame;
    private static JPanel calendarPanel;
    private static JCalendar prevCal;
    private static JCalendar currCal;
    private static JCalendar nextCal;
    private static LocalDate currentMonth;
    private static Map<LocalDate, String> holidays = new HashMap<>();
    private static Map<LocalDate, String> vacations = new HashMap<>();

    static class Holiday {
        String date;
        String name;
    }

    public static void main(String[] args) {
        loadHolidays();

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
        calendarFrame.setSize(800, 400);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton prevButton = new JButton("< Previous");
        JButton nextButton = new JButton("Next >");
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        calendarPanel = new JPanel();
        calendarPanel.setLayout(new BoxLayout(calendarPanel, BoxLayout.X_AXIS));

        prevCal = new JCalendar();
        currCal = new JCalendar();
        nextCal = new JCalendar();

        calendarPanel.add(prevCal);
        calendarPanel.add(currCal);
        calendarPanel.add(nextCal);

        mainPanel.add(calendarPanel, BorderLayout.CENTER);

        calendarFrame.add(mainPanel);

        currentMonth = LocalDate.now().withDayOfMonth(1);
        updateCalendars();

        prevButton.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendars();
        });

        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendars();
        });

        addCalendarListeners();

        calendarFrame.setVisible(false);
    }

    private static void updateCalendars() {
        prevCal.setDate(java.util.Date.from(currentMonth.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        currCal.setDate(java.util.Date.from(currentMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        nextCal.setDate(java.util.Date.from(currentMonth.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private static void addCalendarListeners() {
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCalendar cal = (JCalendar) e.getSource();
                LocalDate selectedDate = LocalDate.ofInstant(cal.getDate().toInstant(), ZoneId.systemDefault());
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
            }
        };

        prevCal.addPropertyChangeListener("calendar", evt -> listener.actionPerformed(new ActionEvent(prevCal, ActionEvent.ACTION_PERFORMED, null)));
        currCal.addPropertyChangeListener("calendar", evt -> listener.actionPerformed(new ActionEvent(currCal, ActionEvent.ACTION_PERFORMED, null)));
        nextCal.addPropertyChangeListener("calendar", evt -> listener.actionPerformed(new ActionEvent(nextCal, ActionEvent.ACTION_PERFORMED, null)));
    }

    private static void loadHolidays() {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 1; year <= currentYear + 1; year++) {
            try {
                String url = "https://date.nager.at/api/v3/PublicHolidays/" + year + "/US";
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                List<Holiday> holidayList = gson.fromJson(response.body(), new TypeToken<List<Holiday>>(){}.getType());
                for (Holiday h : holidayList) {
                    LocalDate date = LocalDate.parse(h.date);
                    holidays.put(date, h.name);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void showCalendar() {
        calendarFrame.setVisible(true);
        calendarFrame.toFront();
    }
}