package com.travelmate.travelmate.repository;

import com.travelmate.travelmate.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByCityIgnoreCase(String city);
}