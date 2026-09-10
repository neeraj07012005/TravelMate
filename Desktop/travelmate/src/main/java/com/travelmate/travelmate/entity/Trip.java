package com.travelmate.travelmate.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    private String destination;
    private String travelDate;
    private String returnDate;
    private int duration;

    public Trip() {
    }

    public Trip(String source, String destination,
                String travelDate, String returnDate,
                int duration) {

        this.source = source;
        this.destination = destination;
        this.travelDate = travelDate;
        this.returnDate = returnDate;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}