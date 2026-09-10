package com.travelmate.travelmate.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class WeatherApiService {

    private final RestClient restClient;

    public WeatherApiService() {
        this.restClient = RestClient.create();
    }

    public Map<String, Object> getWeather(String destination) {

        double latitude;
        double longitude;

        if (destination.equalsIgnoreCase("Goa")) {
            latitude = 15.49;
            longitude = 73.83;

        } else if (destination.equalsIgnoreCase("Delhi")) {
            latitude = 28.61;
            longitude = 77.21;

        } else if (destination.equalsIgnoreCase("Mumbai")) {
            latitude = 19.07;
            longitude = 72.87;

        } else if (destination.equalsIgnoreCase("Bangalore")) {
            latitude = 12.97;
            longitude = 77.59;

        } else if (destination.equalsIgnoreCase("Chennai")) {
            latitude = 13.08;
            longitude = 80.27;

        } else {
            // Default location: Goa
            latitude = 15.49;
            longitude = 73.83;
        }

        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + latitude
                + "&longitude=" + longitude
                + "&current=temperature_2m,weather_code,wind_speed_10m";

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(Map.class);
    }
}