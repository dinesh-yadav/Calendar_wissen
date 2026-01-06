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
import java.util.logging.Level;
import java.util.logging.Logger;

public class HolidayService {
    private static final Logger logger = Logger.getLogger(HolidayService.class.getName());
    private final HttpClient httpClient;
    private final Gson gson;

    public HolidayService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        logger.info("HolidayService initialized");
    }

    public Map<LocalDate, List<Holiday>> loadHolidays() {
        logger.info("Starting holiday data loading");
        Map<LocalDate, List<Holiday>> holidays = new HashMap<>();
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 1; year <= currentYear + 1; year++) {
            try {
                String url = "https://date.nager.at/api/v3/PublicHolidays/" + year + "/US";
                logger.fine("Fetching holidays for year: " + year + " from URL: " + url);
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    logger.warning("Failed to fetch holidays for year " + year + ". HTTP status: " + response.statusCode());
                    continue;
                }

                List<Holiday> holidayList = gson.fromJson(response.body(), new TypeToken<List<Holiday>>(){}.getType());
                logger.info("Fetched " + holidayList.size() + " holidays for year " + year);

                for (Holiday h : holidayList) {
                    h.setType("regular");
                    logger.fine("Processing holiday: " + h.getName() + " on " + h.getDate());
                    // Example: mark some as work holidays
                    if (h.getName().contains("Christmas") || h.getName().contains("Thanksgiving")) {
                        h.setType("work");
                        logger.fine("Marked as work holiday: " + h.getName());
                    }
                    LocalDate date = LocalDate.parse(h.getDate());
                    holidays.computeIfAbsent(date, k -> new ArrayList<>()).add(h);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "IO error while fetching holidays for year " + year, e);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Interrupted while fetching holidays for year " + year, e);
                Thread.currentThread().interrupt(); // Restore interrupted status
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unexpected error while processing holidays for year " + year, e);
            }
        }
        logger.info("Holiday loading completed. Total holiday dates loaded: " + holidays.size());
        return holidays;
    }
}