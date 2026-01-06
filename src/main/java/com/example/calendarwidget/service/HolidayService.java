package com.example.calendarwidget.service;

import com.example.calendarwidget.model.Holiday;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidayService {
    private final HttpClient httpClient;
    private final Gson gson;

    public HolidayService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public Map<LocalDate, List<Holiday>> loadHolidays() {
        Map<LocalDate, List<Holiday>> holidays = new HashMap<>();
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 1; year <= currentYear + 1; year++) {
            try {
                String url = "https://date.nager.at/api/v3/PublicHolidays/" + year + "/US";
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                List<Holiday> holidayList = gson.fromJson(response.body(), new TypeToken<List<Holiday>>(){}.getType());
                System.out.println("Fetched " + holidayList.size() + " holidays for year " + year + ":");
                for (Holiday h : holidayList) {
                    h.setType("regular");
                    System.out.println("  Holiday: " + h.getName() + " on " + h.getDate());
                    // Example: mark some as work holidays
                    if (h.getName().contains("Christmas") || h.getName().contains("Thanksgiving")) {
                        h.setType("work");
                        System.out.println("    Marked as work holiday");
                    }
                    LocalDate date = LocalDate.parse(h.getDate());
                    holidays.computeIfAbsent(date, k -> new ArrayList<>()).add(h);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Total holiday dates loaded: " + holidays.size());
        return holidays;
    }
}