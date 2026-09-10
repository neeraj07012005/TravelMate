package com.travelmate.travelmate.repository;

import com.travelmate.travelmate.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}