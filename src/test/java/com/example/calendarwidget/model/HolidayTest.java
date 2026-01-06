package com.example.calendarwidget.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HolidayTest {

    @Test
    public void testHolidayCreation() {
        Holiday holiday = new Holiday("2026-01-01", "New Year's Day", "regular");
        assertEquals("2026-01-01", holiday.getDate());
        assertEquals("New Year's Day", holiday.getName());
        assertEquals("regular", holiday.getType());
    }

    @Test
    public void testHolidaySetters() {
        Holiday holiday = new Holiday();
        holiday.setDate("2026-12-25");
        holiday.setName("Christmas Day");
        holiday.setType("work");

        assertEquals("2026-12-25", holiday.getDate());
        assertEquals("Christmas Day", holiday.getName());
        assertEquals("work", holiday.getType());
    }

    @Test
    public void testHolidayDefaultConstructor() {
        Holiday holiday = new Holiday();
        assertNull(holiday.getDate());
        assertNull(holiday.getName());
        assertNull(holiday.getType());
    }
}