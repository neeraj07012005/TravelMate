package com.travelmate.travelmate.model;

import com.travelmate.travelmate.entity.Trip;
import com.travelmate.travelmate.entity.Place;

import java.util.List;
import java.util.Map;

public class TravelResult {

    private Trip trip;
    private Map<String, Object> weather;
    private List<Place> places;
    private Map<String, Object> flights;

    public TravelResult(
            Trip trip,
            Map<String, Object> weather,
            List<Place> places,
            Map<String, Object> flights) {

        this.trip = trip;
        this.weather = weather;
        this.places = places;
        this.flights = flights;
    }

    public Trip getTrip() {
        return trip;
    }

    public Map<String, Object> getWeather() {
        return weather;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public Map<String, Object> getFlights() {
        return flights;
    }
}