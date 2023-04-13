package com.example.travelapp.itineraries

import java.io.File

class Itinerary(
    var name: String,
    var id: String,
    var rating: Float = 0f,
    var numberOfRate: Int = 0
    ) : java.io.Serializable  {
    val steps: ArrayList<Step> = ArrayList()
    val itineraryId: String = id

    fun calculateItineraryPrice(): Double {
        var totalPrice = 0.0
        for (step in steps) {
            totalPrice += step.price
        }
        return totalPrice
    }
}