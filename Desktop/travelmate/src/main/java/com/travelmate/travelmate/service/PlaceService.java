package com.travelmate.travelmate.service;

import com.travelmate.travelmate.entity.Place;
import com.travelmate.travelmate.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public List<Place> getPlaces(String city) {
        return placeRepository.findByCityIgnoreCase(city);
    }
}