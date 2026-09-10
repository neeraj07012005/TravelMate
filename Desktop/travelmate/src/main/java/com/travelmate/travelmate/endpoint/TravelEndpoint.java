package com.travelmate.travelmate.endpoint;

import com.travelmate.travelmate.entity.Place;
import com.travelmate.travelmate.model.TravelResult;
import com.travelmate.travelmate.service.TravelService;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

import java.util.List;
import java.util.Map;

@Endpoint
public class TravelEndpoint {

    private static final String NAMESPACE =
            "http://travelmate.com/travel";

    private final TravelService travelService;

    public TravelEndpoint(TravelService travelService) {
        this.travelService = travelService;
    }

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "getTravelInfoRequest"
    )
    @ResponsePayload
    public Element getTravelInfo(
            @RequestPayload Element request) {

        try {

            String source = request
                    .getElementsByTagNameNS(NAMESPACE, "source")
                    .item(0)
                    .getTextContent();

            String destination = request
                    .getElementsByTagNameNS(NAMESPACE, "destination")
                    .item(0)
                    .getTextContent();

            String travelDate = request
                    .getElementsByTagNameNS(NAMESPACE, "travelDate")
                    .item(0)
                    .getTextContent();

            String returnDate = request
                    .getElementsByTagNameNS(NAMESPACE, "returnDate")
                    .item(0)
                    .getTextContent();

            int duration = Integer.parseInt(
                    request
                            .getElementsByTagNameNS(NAMESPACE, "duration")
                            .item(0)
                            .getTextContent()
            );

            TravelResult result = travelService.saveTrip(
                    source,
                    destination,
                    travelDate,
                    returnDate,
                    duration
            );

            // ---------------- WEATHER ----------------

            Map<String, Object> weather =
                    result.getWeather();

            Map<String, Object> current =
                    (Map<String, Object>) weather.get("current");

            double temperature =
                    ((Number) current.get("temperature_2m"))
                            .doubleValue();

            double windSpeed =
                    ((Number) current.get("wind_speed_10m"))
                            .doubleValue();

            int weatherCode =
                    ((Number) current.get("weather_code"))
                            .intValue();

            // ---------------- PLACES ----------------

            List<Place> places =
                    result.getPlaces();

            // ---------------- FLIGHTS ----------------

            Map<String, Object> flights =
                    result.getFlights();

            double flightPrice =
                    ((Number) flights.get("price"))
                            .doubleValue();

            String flightDuration =
                    String.valueOf(
                            flights.get("duration")
                    );

            // ---------------- XML DOCUMENT ----------------

            Document document =
                    DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                            .newDocument();

            Element response =
                    document.createElementNS(
                            NAMESPACE,
                            "getTravelInfoResponse"
                    );

            // MESSAGE

            Element message =
                    document.createElementNS(
                            NAMESPACE,
                            "message"
                    );

            message.setTextContent(
                    "Trip saved successfully!"
            );

            // SOURCE

            Element sourceElement =
                    document.createElementNS(
                            NAMESPACE,
                            "source"
                    );

            sourceElement.setTextContent(source);

            // DESTINATION

            Element destinationElement =
                    document.createElementNS(
                            NAMESPACE,
                            "destination"
                    );

            destinationElement.setTextContent(
                    destination
            );

            // TRAVEL DATE

            Element travelDateElement =
                    document.createElementNS(
                            NAMESPACE,
                            "travelDate"
                    );

            travelDateElement.setTextContent(
                    travelDate
            );

            // RETURN DATE

            Element returnDateElement =
                    document.createElementNS(
                            NAMESPACE,
                            "returnDate"
                    );

            returnDateElement.setTextContent(
                    returnDate
            );

            // DURATION

            Element durationElement =
                    document.createElementNS(
                            NAMESPACE,
                            "duration"
                    );

            durationElement.setTextContent(
                    String.valueOf(duration)
            );

            // TEMPERATURE

            Element temperatureElement =
                    document.createElementNS(
                            NAMESPACE,
                            "temperature"
                    );

            temperatureElement.setTextContent(
                    String.valueOf(temperature)
            );

            // WEATHER CODE

            Element weatherCodeElement =
                    document.createElementNS(
                            NAMESPACE,
                            "weatherCode"
                    );

            weatherCodeElement.setTextContent(
                    String.valueOf(weatherCode)
            );

            // WIND

            Element windElement =
                    document.createElementNS(
                            NAMESPACE,
                            "windSpeed"
                    );

            windElement.setTextContent(
                    String.valueOf(windSpeed)
            );

            // ---------------- PLACES XML ----------------

            Element placesElement =
                    document.createElementNS(
                            NAMESPACE,
                            "places"
                    );

            for (Place place : places) {

                Element placeElement =
                        document.createElementNS(
                                NAMESPACE,
                                "place"
                        );

                Element nameElement =
                        document.createElementNS(
                                NAMESPACE,
                                "name"
                        );

                nameElement.setTextContent(
                        place.getName()
                );

                Element descriptionElement =
                        document.createElementNS(
                                NAMESPACE,
                                "description"
                        );

                descriptionElement.setTextContent(
                        place.getDescription()
                );

                placeElement.appendChild(
                        nameElement
                );

                placeElement.appendChild(
                        descriptionElement
                );

                placesElement.appendChild(
                        placeElement
                );
            }

            // ---------------- FLIGHT PRICE ----------------

            Element flightPriceElement =
                    document.createElementNS(
                            NAMESPACE,
                            "flightPrice"
                    );

            flightPriceElement.setTextContent(
                    String.valueOf(flightPrice)
            );

            // ---------------- FLIGHT DURATION ----------------

            Element flightDurationElement =
                    document.createElementNS(
                            NAMESPACE,
                            "flightDuration"
                    );

            flightDurationElement.setTextContent(
                    flightDuration
            );

            // ---------------- SAVED ----------------

            Element savedElement =
                    document.createElementNS(
                            NAMESPACE,
                            "saved"
                    );

            savedElement.setTextContent("true");

            // ---------------- BUILD RESPONSE ----------------

            response.appendChild(message);
            response.appendChild(sourceElement);
            response.appendChild(destinationElement);
            response.appendChild(travelDateElement);
            response.appendChild(returnDateElement);
            response.appendChild(durationElement);

            response.appendChild(temperatureElement);
            response.appendChild(weatherCodeElement);
            response.appendChild(windElement);

            response.appendChild(placesElement);

            response.appendChild(flightPriceElement);
            response.appendChild(flightDurationElement);

            response.appendChild(savedElement);

            document.appendChild(response);

            return response;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error processing travel request",
                    e
            );
        }
    }
}