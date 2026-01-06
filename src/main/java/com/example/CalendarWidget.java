package com.example;

import com.toedter.calendar.JCalendar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import java.time.temporal.WeekFields;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
    private static Map<LocalDate, List<Holiday>> holidays = new HashMap<>();
    private static Map<LocalDate, String> vacations = new HashMap<>();

    static class Holiday {
        String date;
        String name;
        String type; // "regular" or "work"
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

        MenuItem autoStartItem = new MenuItem("Enable Auto-start");
        autoStartItem.addActionListener(e -> {
            try {
                Path source = Paths.get("run.bat");
                Path target = Paths.get(System.getenv("APPDATA"), "Microsoft", "Windows", "Start Menu", "Programs", "Startup", "CalendarWidget.bat");
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(calendarFrame, "Auto-start enabled. The app will start with Windows.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(calendarFrame, "Failed to enable auto-start: " + ex.getMessage());
            }
        });
        menu.add(autoStartItem);

        MenuItem disableItem = new MenuItem("Disable Auto-start");
        disableItem.addActionListener(e -> {
            try {
                Path target = Paths.get(System.getenv("APPDATA"), "Microsoft", "Windows", "Start Menu", "Programs", "Startup", "CalendarWidget.bat");
                Files.deleteIfExists(target);
                JOptionPane.showMessageDialog(calendarFrame, "Auto-start disabled.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(calendarFrame, "Failed to disable auto-start: " + ex.getMessage());
            }
        });
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

        // calendarFrame.setVisible(false);
        showCalendar();
    }

    private static void updateCalendars() {
        prevCal.setDate(java.util.Date.from(currentMonth.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        highlightWeeks(prevCal);
        currCal.setDate(java.util.Date.from(currentMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        highlightWeeks(currCal);
        nextCal.setDate(java.util.Date.from(currentMonth.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        highlightWeeks(nextCal);
    }

    private static void addCalendarListeners() {
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCalendar cal = (JCalendar) e.getSource();
                LocalDate selectedDate = LocalDate.ofInstant(cal.getDate().toInstant(), ZoneId.systemDefault());
                StringBuilder info = new StringBuilder();
                List<Holiday> dayHolidays = holidays.get(selectedDate);
                if (dayHolidays != null) {
                    // Sort to prioritize work holidays
                    dayHolidays.sort((a, b) -> {
                        if ("work".equals(a.type) && !"work".equals(b.type)) return -1;
                        if (!"work".equals(a.type) && "work".equals(b.type)) return 1;
                        return 0;
                    });
                    for (Holiday h : dayHolidays) {
                        info.append(h.type.equals("work") ? "Work Holiday: " : "Holiday: ").append(h.name).append("\n");
                    }
                }
                if (vacations.containsKey(selectedDate)) {
                    info.append("Vacation: ").append(vacations.get(selectedDate)).append("\n");
                }
                if (info.length() == 0) {
                    info.append("No special events");
                }
                JOptionPane.showMessageDialog(calendarFrame, info.toString().trim());
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
        for (int year = currentYear; year <= currentYear + 1; year++) {
            try {
                String url = "https://date.nager.at/api/v3/PublicHolidays/" + year + "/US";
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                List<Holiday> holidayList = gson.fromJson(response.body(), new TypeToken<List<Holiday>>(){}.getType());
                for (Holiday h : holidayList) {
                    h.type = "regular";
                    // Example: mark some as work holidays
                    if (h.name.contains("Christmas") || h.name.contains("Thanksgiving")) {
                        h.type = "work";
                    }
                    LocalDate date = LocalDate.parse(h.date);
                    holidays.computeIfAbsent(date, k -> new ArrayList<>()).add(h);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void highlightWeeks(JCalendar cal) {
        LocalDate monthStart = LocalDate.ofInstant(cal.getDate().toInstant(), ZoneId.systemDefault()).withDayOfMonth(1);
        int year = monthStart.getYear();
        int month = monthStart.getMonthValue();
        Map<Integer, Integer> weekCounts = new HashMap<>();
        for (int day = 1; day <= monthStart.lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            List<Holiday> hs = holidays.get(date);
            if (hs != null) {
                for (Holiday h : hs) {
                    if ("work".equals(h.type)) {
                        int week = date.get(WeekFields.ISO.weekOfYear());
                        weekCounts.put(week, weekCounts.getOrDefault(week, 0) + 1);
                    }
                }
            }
        }
        com.toedter.calendar.JDayChooser dayChooser = cal.getDayChooser();
        Component[] components = dayChooser.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String text = button.getText();
                if (!text.isEmpty()) {
                    try {
                        int day = Integer.parseInt(text);
                        if (day >= 1 && day <= monthStart.lengthOfMonth()) {
                            LocalDate date = LocalDate.of(year, month, day);
                            int week = date.get(WeekFields.ISO.weekOfYear());
                            Integer count = weekCounts.get(week);
                            if (count != null) {
                                button.setOpaque(true);
                                if (count == 1) {
                                    button.setBackground(Color.CYAN);
                                } else {
                                    button.setBackground(Color.BLUE);
                                }
                            } else {
                                button.setBackground(null);
                                button.setOpaque(false);
                            }
                        }
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        }
    }

    private static void showCalendar() {
        calendarFrame.setVisible(true);
        calendarFrame.toFront();
    }
}