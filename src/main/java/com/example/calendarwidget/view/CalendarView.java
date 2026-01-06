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

public class CalendarView extends JPanel {
    private JCalendar prevCal;
    private JCalendar currCal;
    private JCalendar nextCal;
    private Map<LocalDate, List<Holiday>> holidays;

    public CalendarView(Map<LocalDate, List<Holiday>> holidays) {
        this.holidays = holidays;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        prevCal = new JCalendar();
        currCal = new JCalendar();
        nextCal = new JCalendar();

        add(prevCal);
        add(currCal);
        add(nextCal);
    }

    public void updateCalendars(LocalDate currentMonth) {
        prevCal.setDate(java.util.Date.from(currentMonth.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        highlightWeeks(prevCal, currentMonth.minusMonths(1));
        currCal.setDate(java.util.Date.from(currentMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        highlightWeeks(currCal, currentMonth);
        nextCal.setDate(java.util.Date.from(currentMonth.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        highlightWeeks(nextCal, currentMonth.plusMonths(1));
    }

    private void highlightWeeks(JCalendar cal, LocalDate month) {
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
        com.toedter.calendar.JDayChooser dayChooser = cal.getDayChooser();
        Component[] components = dayChooser.getComponents();
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

    public JCalendar getPrevCal() { return prevCal; }
    public JCalendar getCurrCal() { return currCal; }
    public JCalendar getNextCal() { return nextCal; }
}