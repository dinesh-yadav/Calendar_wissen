package com.example.calendarwidget.view;

import com.example.calendarwidget.model.Holiday;
import com.toedter.calendar.JCalendar;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalendarView extends JPanel {
    private static final Logger logger = Logger.getLogger(CalendarView.class.getName());
    private JCalendar prevCal;
    private JCalendar currCal;
    private JCalendar nextCal;
    private Map<LocalDate, List<Holiday>> holidays;

    public CalendarView(Map<LocalDate, List<Holiday>> holidays) {
        logger.info("Initializing CalendarView");
        this.holidays = holidays;
        initializeComponents();
        logger.info("CalendarView initialized successfully");
    }

    private void initializeComponents() {
        logger.fine("Initializing calendar components");
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        prevCal = new JCalendar();
        currCal = new JCalendar();
        nextCal = new JCalendar();

        add(prevCal);
        add(currCal);
        add(nextCal);
        logger.fine("Calendar components initialized");
    }

    public void updateCalendars(LocalDate currentMonth) {
        logger.fine("Updating calendars for month: " + currentMonth);
        try {
            prevCal.setDate(java.util.Date.from(currentMonth.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            highlightWeeks(prevCal, currentMonth.minusMonths(1));
            currCal.setDate(java.util.Date.from(currentMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            highlightWeeks(currCal, currentMonth);
            nextCal.setDate(java.util.Date.from(currentMonth.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            highlightWeeks(nextCal, currentMonth.plusMonths(1));
            logger.fine("Calendars updated successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating calendars for month: " + currentMonth, e);
        }
    }

    private void highlightWeeks(JCalendar cal, LocalDate month) {
        logger.fine("Highlighting weeks for month: " + month);
        try {
            int year = month.getYear();
            int monthValue = month.getMonthValue();
            Map<Integer, Integer> weekCounts = new HashMap<>();

            for (int day = 1; day <= month.lengthOfMonth(); day++) {
                LocalDate date = LocalDate.of(year, monthValue, day);
                List<Holiday> hs = holidays.get(date);
                if (hs != null) {
                    for (Holiday h : hs) {
                        if ("work".equals(h.getType())) {
                            int week = date.get(WeekFields.ISO.weekOfYear());
                            weekCounts.put(week, weekCounts.getOrDefault(week, 0) + 1);
                        }
                    }
                }
            }

            logger.fine("Found " + weekCounts.size() + " weeks with work holidays for month: " + month);

            com.toedter.calendar.JDayChooser dayChooser = cal.getDayChooser();
            Component[] components = dayChooser.getComponents();
            int highlightedDays = 0;

            for (Component comp : components) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    String text = button.getText();
                    if (!text.isEmpty()) {
                        try {
                            int day = Integer.parseInt(text);
                            if (day >= 1 && day <= month.lengthOfMonth()) {
                                LocalDate date = LocalDate.of(year, monthValue, day);
                                int week = date.get(WeekFields.ISO.weekOfYear());
                                Integer count = weekCounts.get(week);
                                if (count != null) {
                                    if (count == 1) {
                                        button.setForeground(Color.CYAN);
                                    } else {
                                        button.setForeground(Color.BLUE);
                                    }
                                    highlightedDays++;
                                } else {
                                    button.setForeground(Color.BLACK);
                                }
                                List<Holiday> dayHolidays = holidays.get(date);
                                if (dayHolidays != null && !dayHolidays.isEmpty()) {
                                    StringBuilder tooltip = new StringBuilder();
                                    for (Holiday h : dayHolidays) {
                                        tooltip.append(h.getType().equals("work") ? "Work Holiday: " : "Holiday: ").append(h.getName()).append("\n");
                                    }
                                    button.setToolTipText(tooltip.toString().trim());
                                    Font font = button.getFont();
                                    button.setFont(font.deriveFont(Font.BOLD));
                                } else {
                                    button.setToolTipText(null);
                                    Font font = button.getFont();
                                    button.setFont(font.deriveFont(Font.PLAIN));
                                }
                            }
                        } catch (NumberFormatException e) {
                            logger.fine("Ignoring non-numeric button text: " + text);
                        }
                    }
                }
            }
            logger.fine("Highlighted " + highlightedDays + " days for month: " + month);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error highlighting weeks for month: " + month, e);
        }
    }

    public JCalendar getPrevCal() { return prevCal; }
    public JCalendar getCurrCal() { return currCal; }
    public JCalendar getNextCal() { return nextCal; }
}