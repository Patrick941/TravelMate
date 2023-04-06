package com.example.mapstemplate

// Top-level response object from the Directions API. Which contains a list of Route objects.
data class DirectionsApiResponse(
    val routes: List<Route>
)
//Represents a single route from the Directions API response. It contains a list of Leg objects.
data class Route(
    val legs: List<Leg>
)
//Represents a single leg within a route. It contains a list of Step objects.
data class Leg(
    val steps: List<Step>
)
// contains the start and end 
//locations as LatLng objects and the encoded polyline as a Polyline object.
data class Step(
    val start_location: LatLng,
    val end_location: LatLng,
    val polyline: Polyline
)
//This is the latitude and longitude coordinate pair. 
data class LatLng(
    val lat: Double,
    val lng: Double
)
//Encoded polyline
data class Polyline(
    val points: String
)
