package com.travelmate.travelmate.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherApiService {

    private final RestClient restClient;

    public WeatherApiService() {
        this.restClient = RestClient.create();
    }

    public Map<String, Object> getWeather(String destination) {

        double latitude = 15.49;
        double longitude = 73.83;

        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + latitude
                + "&longitude=" + longitude
                + "&current=temperature_2m,weather_code,wind_speed_10m";

        try {

            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(Map.class);

        } catch (Exception e) {

            System.err.println(
                    "Weather API unavailable. Using fallback weather."
            );

            Map<String, Object> current =
                    new HashMap<>();

            current.put("temperature_2m", 30.0);
            current.put("weather_code", 0);
            current.put("wind_speed_10m", 5.0);

            Map<String, Object> fallback =
                    new HashMap<>();

            fallback.put("current", current);

            return fallback;
        }
    }
}