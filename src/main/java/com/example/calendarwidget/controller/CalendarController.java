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
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalendarController {
    private static final Logger logger = Logger.getLogger(CalendarController.class.getName());
    private CalendarView calendarView;
    private Map<LocalDate, List<Holiday>> holidays;
    private LocalDate currentMonth;

    public CalendarController(CalendarView calendarView, Map<LocalDate, List<Holiday>> holidays) {
        logger.info("Initializing CalendarController");
        this.calendarView = calendarView;
        this.holidays = holidays;
        this.currentMonth = LocalDate.now().withDayOfMonth(1);
        logger.fine("Current month set to: " + currentMonth);
        calendarView.updateCalendars(currentMonth);
        setupListeners();
        logger.info("CalendarController initialized successfully");
    }

    private void setupListeners() {
        logger.fine("Setting up calendar listeners");
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JCalendar cal = (JCalendar) e.getSource();
                    LocalDate selectedDate = LocalDate.ofInstant(cal.getDate().toInstant(), ZoneId.systemDefault());
                    logger.fine("Date selected: " + selectedDate);

                    StringBuilder info = new StringBuilder();
                    List<Holiday> dayHolidays = holidays.get(selectedDate);
                    if (dayHolidays != null) {
                        logger.fine("Found " + dayHolidays.size() + " holidays for date: " + selectedDate);
                        // Sort to prioritize work holidays
                        dayHolidays.sort((a, b) -> {
                            if ("work".equals(a.getType()) && !"work".equals(b.getType())) return -1;
                            if (!"work".equals(a.getType()) && "work".equals(b.getType())) return 1;
                            return 0;
                        });
                        for (Holiday h : dayHolidays) {
                            info.append(h.getType().equals("work") ? "Work Holiday: " : "Holiday: ").append(h.getName()).append("\n");
                        }
                    } else {
                        logger.fine("No holidays found for date: " + selectedDate);
                    }
                    // Add vacation info if needed
                    if (info.length() == 0) {
                        info.append("No special events");
                    }

                    JOptionPane.showMessageDialog(calendarView, info.toString().trim());
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error handling date selection", ex);
                    JOptionPane.showMessageDialog(calendarView, "Error displaying holiday information: " + ex.getMessage());
                }
            }
        };

        try {
            calendarView.getPrevCal().addPropertyChangeListener("calendar", evt -> listener.actionPerformed(new ActionEvent(calendarView.getPrevCal(), ActionEvent.ACTION_PERFORMED, null)));
            calendarView.getCurrCal().addPropertyChangeListener("calendar", evt -> listener.actionPerformed(new ActionEvent(calendarView.getCurrCal(), ActionEvent.ACTION_PERFORMED, null)));
            calendarView.getNextCal().addPropertyChangeListener("calendar", evt -> listener.actionPerformed(new ActionEvent(calendarView.getNextCal(), ActionEvent.ACTION_PERFORMED, null)));
            logger.fine("Calendar listeners set up successfully");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error setting up calendar listeners", ex);
        }
    }

    public void navigatePrevious() {
        logger.fine("Navigating to previous month from: " + currentMonth);
        currentMonth = currentMonth.minusMonths(1);
        logger.fine("New current month: " + currentMonth);
        calendarView.updateCalendars(currentMonth);
    }

    public void navigateNext() {
        logger.fine("Navigating to next month from: " + currentMonth);
        currentMonth = currentMonth.plusMonths(1);
        logger.fine("New current month: " + currentMonth);
        calendarView.updateCalendars(currentMonth);
    }
}