package com.travelmate.travelmate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class FlightApiService {

    private final RestClient restClient;

    @Value("${serpapi.key}")
    private String apiKey;

    public FlightApiService() {
        this.restClient = RestClient.create();
    }

    public Map<String, Object> searchFlights(
            String source,
            String destination,
            String departureDate,
            String returnDate) {

        String url = "https://serpapi.com/search.json"
                + "?engine=google_flights"
                + "&departure_id=" + getAirportCode(source)
                + "&arrival_id=" + getAirportCode(destination)
                + "&outbound_date=" + departureDate
                + "&return_date=" + returnDate
                + "&currency=INR"
                + "&hl=en"
                + "&api_key=" + apiKey;

        Map<String, Object> response = restClient.get()
                .uri(url)
                .retrieve()
                .body(Map.class);

        return getCheapestFlight(response);
    }

    private Map<String, Object> getCheapestFlight(
            Map<String, Object> response) {

        Map<String, Object> cheapest = null;

        List<Map<String, Object>> flights =
                (List<Map<String, Object>>) response.get("best_flights");

        if (flights == null) {
            flights =
                (List<Map<String, Object>>) response.get("other_flights");
        }

        if (flights == null) {
            return Map.of(
                    "price", 0,
                    "duration", "Not available"
            );
        }

        for (Map<String, Object> flight : flights) {

            Number price =
                    (Number) flight.get("price");

            if (price == null) {
                continue;
            }

            if (cheapest == null ||
                    price.doubleValue()
                    < ((Number) cheapest.get("price")).doubleValue()) {

                cheapest = flight;
            }
        }

        if (cheapest == null) {
            return Map.of(
                    "price", 0,
                    "duration", "Not available"
            );
        }

        Object duration =
                cheapest.get("total_duration");

        return Map.of(
                "price", ((Number) cheapest.get("price")).doubleValue(),
                "duration", duration != null
                        ? duration.toString()
                        : "Not available"
        );
    }

    private String getAirportCode(String city) {

        Map<String, String> airports = Map.ofEntries(
                Map.entry("delhi", "DEL"),
                Map.entry("mumbai", "BOM"),
                Map.entry("goa", "GOI"),
                Map.entry("bangalore", "BLR"),
                Map.entry("bengaluru", "BLR"),
                Map.entry("chennai", "MAA"),
                Map.entry("hyderabad", "HYD"),
                Map.entry("kolkata", "CCU"),
                Map.entry("jaipur", "JAI"),
                Map.entry("agra", "AGR"),
                Map.entry("pune", "PNQ"),
                Map.entry("ahmedabad", "AMD"),
                Map.entry("kochi", "COK"),
                Map.entry("mysore", "MYQ"),
                Map.entry("udaipur", "UDR"),
                Map.entry("varanasi", "VNS"),
                Map.entry("amritsar", "ATQ"),
                Map.entry("rishikesh", "DED"),
                Map.entry("manali", "KUU"),
                Map.entry("shimla", "SLV"),
                Map.entry("srinagar", "SXR"),
                Map.entry("jodhpur", "JDH"),
                Map.entry("lucknow", "LKO"),
                Map.entry("chandigarh", "IXC"),
                Map.entry("indore", "IDR"),
                Map.entry("bhopal", "BHO"),
                Map.entry("ooty", "CJB"),
                Map.entry("darjeeling", "IXB"),
                Map.entry("pondicherry", "PNY"),
                Map.entry("visakhapatnam", "VTZ"),
                Map.entry("nashik", "ISK")
        );

        String code =
                airports.get(city.trim().toLowerCase());

        if (code == null) {
            throw new IllegalArgumentException(
                    "Flight search unavailable for: " + city
            );
        }

        return code;
    }
}