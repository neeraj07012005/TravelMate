const NAMESPACE = "http://travelmate.com/travel";

async function planTrip() {

    const source =
        document.getElementById("source").value.trim();

    const destination =
        document.getElementById("destination").value.trim();

    const travelDate =
        document.getElementById("travelDate").value;

    const returnDate =
        document.getElementById("returnDate").value;

    const duration =
        document.getElementById("duration").value;


    // ---------------- VALIDATION ----------------

    if (
        !source ||
        !destination ||
        !travelDate ||
        !returnDate ||
        !duration
    ) {
        alert("Please fill all trip details.");
        return;
    }

    if (new Date(returnDate) < new Date(travelDate)) {

        alert(
            "Return date cannot be before departure date."
        );

        return;
    }

    if (Number(duration) <= 0) {

        alert(
            "Duration must be at least 1 day."
        );

        return;
    }


    // ---------------- BUTTON ----------------

    const button =
        document.getElementById("planButton");

    const buttonText =
        document.getElementById("buttonText");

    button.disabled = true;

    buttonText.textContent =
        "Planning your trip...";


    // ---------------- LOADING STATE ----------------

    document.getElementById("tripStatus")
        .textContent =
        "Connecting to TravelMate";

    document.getElementById("temperature")
        .textContent = "...";

    document.getElementById("wind")
        .textContent = "...";

    document.getElementById("weatherDescription")
        .textContent =
        "Fetching weather...";

    document.getElementById("places")
        .innerHTML =
        "<li>Finding popular places...</li>";

    document.getElementById("budget")
        .textContent = "...";

    document.getElementById("flightPrice")
        .textContent = "...";

    document.getElementById("flightDuration")
        .textContent = "...";


    // ---------------- SOAP REQUEST ----------------

    const soapRequest = `
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:tra="http://travelmate.com/travel">

    <soapenv:Header/>

    <soapenv:Body>

        <tra:getTravelInfoRequest>

            <tra:source>${escapeXml(source)}</tra:source>

            <tra:destination>${escapeXml(destination)}</tra:destination>

            <tra:travelDate>${escapeXml(travelDate)}</tra:travelDate>

            <tra:returnDate>${escapeXml(returnDate)}</tra:returnDate>

            <tra:duration>${escapeXml(duration)}</tra:duration>

        </tra:getTravelInfoRequest>

    </soapenv:Body>

</soapenv:Envelope>
`.trim();


    try {

        console.log(
            "Sending SOAP request:"
        );

        console.log(soapRequest);


        // ---------------- SEND SOAP REQUEST ----------------

        const response =
            await fetch("/ws", {

                method: "POST",

                headers: {
                    "Content-Type":
                        "text/xml;charset=UTF-8"
                },

                body: soapRequest

            });


        if (!response.ok) {

            throw new Error(
                "SOAP request failed. HTTP status: "
                + response.status
            );
        }


        // ---------------- READ RESPONSE ----------------

        const responseText =
            await response.text();

        console.log(
            "SOAP response:"
        );

        console.log(responseText);


        // ---------------- PARSE XML ----------------

        const parser =
            new DOMParser();

        const xml =
            parser.parseFromString(
                responseText,
                "text/xml"
            );


        // ---------------- SOAP FAULT ----------------

        const fault =
            xml.getElementsByTagNameNS(
                "http://schemas.xmlsoap.org/soap/envelope/",
                "Fault"
            )[0];

        if (fault) {

            throw new Error(
                fault.textContent
            );
        }


        // ---------------- HELPER ----------------

        function getValue(tagName) {

            const element =
                xml.getElementsByTagNameNS(
                    NAMESPACE,
                    tagName
                )[0];

            return element
                ? element.textContent
                : "";
        }


        // ---------------- TRIP INFORMATION ----------------

        const responseSource =
            getValue("source");

        const responseDestination =
            getValue("destination");


        document.getElementById("tripTitle")
            .textContent =
            `${responseSource} → ${responseDestination}`;


        // ---------------- WEATHER ----------------

        const temperature =
            getValue("temperature");

        const weatherCode =
            Number(
                getValue("weatherCode")
            );

        const windSpeed =
            getValue("windSpeed");


        document.getElementById("temperature")
            .textContent =
            temperature;


        document.getElementById("wind")
            .textContent =
            windSpeed;


        document.getElementById("weatherDescription")
            .textContent =
            getWeatherDescription(
                weatherCode
            );


        // ---------------- POPULAR PLACES ----------------

        const placeElements =
            xml.getElementsByTagNameNS(
                NAMESPACE,
                "place"
            );


        const placesList =
            document.getElementById("places");


        placesList.innerHTML = "";


        if (placeElements.length === 0) {

            placesList.innerHTML =
                "<li>No popular places found.</li>";

        } else {

            for (
                let i = 0;
                i < placeElements.length;
                i++
            ) {

                const place =
                    placeElements[i];


                const nameElement =
                    place.getElementsByTagNameNS(
                        NAMESPACE,
                        "name"
                    )[0];


                const descriptionElement =
                    place.getElementsByTagNameNS(
                        NAMESPACE,
                        "description"
                    )[0];


                const name =
                    nameElement
                        ? nameElement.textContent
                        : "Unknown place";


                const description =
                    descriptionElement
                        ? descriptionElement.textContent
                        : "";


                const li =
                    document.createElement("li");


                const nameStrong =
                    document.createElement("strong");


                nameStrong.textContent =
                    name;


                const descriptionSmall =
                    document.createElement("small");


                descriptionSmall.textContent =
                    description;


                li.appendChild(
                    nameStrong
                );


                li.appendChild(
                    document.createElement("br")
                );


                li.appendChild(
                    descriptionSmall
                );


                placesList.appendChild(li);
            }
        }


        // ---------------- FLIGHT ----------------

        const flightPrice =
            getValue("flightPrice");

        const flightDuration =
            getValue("flightDuration");


        if (
            flightPrice &&
            Number(flightPrice) > 0
        ) {

            document.getElementById("flightPrice")
                .textContent =
                Number(flightPrice)
                    .toLocaleString("en-IN");

        } else {

            document.getElementById("flightPrice")
                .textContent =
                "Not available";
        }


        document.getElementById("flightDuration")
            .textContent =
            flightDuration;


        // ---------------- BUDGET ----------------

        const estimatedBudget =
            Number(duration) * 5000;


        document.getElementById("budget")
            .textContent =
            estimatedBudget.toLocaleString(
                "en-IN"
            );


        // ---------------- STATUS ----------------

        const saved =
            getValue("saved");


        if (saved === "true") {

            document.getElementById("tripStatus")
                .textContent =
                "✓ Trip planned";

        } else {

            document.getElementById("tripStatus")
                .textContent =
                "Trip processed";
        }


        // ---------------- SCROLL ----------------

        document.getElementById("results")
            .scrollIntoView({
                behavior: "smooth"
            });


    } catch (error) {

        console.error(
            "TravelMate error:",
            error
        );


        document.getElementById("tripStatus")
            .textContent =
            "Connection failed";


        document.getElementById("places")
            .innerHTML =
            "<li>Unable to load trip information.</li>";


        document.getElementById("flightPrice")
            .textContent =
            "Unavailable";


        document.getElementById("flightDuration")
            .textContent =
            "--";


        alert(
            "Unable to connect to TravelMate backend.\n\n" +
            "Check the browser console and Spring Boot terminal."
        );


    } finally {

        button.disabled = false;

        buttonText.textContent =
            "Plan My Trip";
    }
}


// =====================================================
// WEATHER DESCRIPTION
// =====================================================

function getWeatherDescription(code) {

    const weatherCodes = {

        0: "Clear sky",

        1: "Mainly clear",

        2: "Partly cloudy",

        3: "Overcast",

        45: "Fog",

        48: "Depositing rime fog",

        51: "Light drizzle",

        53: "Moderate drizzle",

        55: "Dense drizzle",

        61: "Slight rain",

        63: "Moderate rain",

        65: "Heavy rain",

        71: "Slight snow",

        73: "Moderate snow",

        75: "Heavy snow",

        80: "Slight rain showers",

        81: "Moderate rain showers",

        82: "Violent rain showers",

        95: "Thunderstorm",

        96: "Thunderstorm with hail",

        99: "Thunderstorm with heavy hail"
    };


    return weatherCodes[code] ||
        "Weather information unavailable";
}


// =====================================================
// ESCAPE XML
// =====================================================

function escapeXml(value) {

    return String(value)

        .replace(
            /&/g,
            "&amp;"
        )

        .replace(
            /</g,
            "&lt;"
        )

        .replace(
            />/g,
            "&gt;"
        )

        .replace(
            /"/g,
            "&quot;"
        )

        .replace(
            /'/g,
            "&apos;"
        );
}