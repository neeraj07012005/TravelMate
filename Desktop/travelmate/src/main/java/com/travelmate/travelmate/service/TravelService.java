package com.travelmate.travelmate.service;

import com.travelmate.travelmate.entity.Trip;
import com.travelmate.travelmate.entity.Place;
import com.travelmate.travelmate.model.TravelResult;
import com.travelmate.travelmate.repository.TripRepository;
import com.travelmate.travelmate.repository.PlaceRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TravelService {

    private final TripRepository tripRepository;
    private final PlaceRepository placeRepository;
    private final WeatherApiService weatherApiService;
    private final FlightApiService flightApiService;

    public TravelService(
            TripRepository tripRepository,
            PlaceRepository placeRepository,
            WeatherApiService weatherApiService,
            FlightApiService flightApiService) {

        this.tripRepository = tripRepository;
        this.placeRepository = placeRepository;
        this.weatherApiService = weatherApiService;
        this.flightApiService = flightApiService;
    }

    public TravelResult saveTrip(
            String source,
            String destination,
            String travelDate,
            String returnDate,
            int duration) {

        // Get weather
        Map<String, Object> weather =
                weatherApiService.getWeather(destination);

        // Get popular places
        List<Place> places =
                placeRepository.findByCityIgnoreCase(destination);

        // Get flight offers
        Map<String, Object> flights =
                flightApiService.searchFlights(
                        source,
                        destination,
                        travelDate,
                        returnDate
                );

        // Save trip
        Trip trip = new Trip(
                source,
                destination,
                travelDate,
                returnDate,
                duration
        );

        Trip savedTrip =
                tripRepository.save(trip);

        return new TravelResult(
                savedTrip,
                weather,
                places,
                flights
        );
    }
}