package com.example.calendarwidget.controller;

import com.example.calendarwidget.model.Holiday;
import com.example.calendarwidget.view.CalendarView;
import com.toedter.calendar.JCalendar;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

public class CalendarController {
    private CalendarView calendarView;
    private Map<LocalDate, List<Holiday>> holidays;
    private LocalDate currentMonth;

    public CalendarController(CalendarView calendarView, Map<LocalDate, List<Holiday>> holidays) {
        this.calendarView = calendarView;
        this.holidays = holidays;
        this.currentMonth = LocalDate.now().withDayOfMonth(1);
        calendarView.updateCalendars(currentMonth);
        setupListeners();
    }

    private void setupListeners() {
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
                        if ("work".equals(a.getType()) && !"work".equals(b.getType())) return -1;
                        if (!"work".equals(a.getType()) && "work".equals(b.getType())) return 1;
                        return 0;
                    });
                    for (Holiday h : dayHolidays) {
                        info.append(h.getType().equals("work") ? "Work Holiday: " : "Holiday: ").append(h.getName()).append("\n");
                    }
                }
                // Add vacation info if needed
                if (info.length() == 0) {
                    info.append("No special events");
                }
                JOptionPane.showMessageDialog(calendarView, info.toString().trim());
            }
        };

        calendarView.getPrevCal().addPropertyChangeListener("calendar", evt -> listener.actionPerformed(new ActionEvent(calendarView.getPrevCal(), ActionEvent.ACTION_PERFORMED, null)));
        calendarView.getCurrCal().addPropertyChangeListener("calendar", evt -> listener.actionPerformed(new ActionEvent(calendarView.getCurrCal(), ActionEvent.ACTION_PERFORMED, null)));
        calendarView.getNextCal().addPropertyChangeListener("calendar", evt -> listener.actionPerformed(new ActionEvent(calendarView.getNextCal(), ActionEvent.ACTION_PERFORMED, null)));
    }

    public void navigatePrevious() {
        currentMonth = currentMonth.minusMonths(1);
        calendarView.updateCalendars(currentMonth);
    }

    public void navigateNext() {
        currentMonth = currentMonth.plusMonths(1);
        calendarView.updateCalendars(currentMonth);
    }
}