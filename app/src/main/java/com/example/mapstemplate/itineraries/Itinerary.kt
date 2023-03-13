package com.example.travelapp.itineraries

class Itinerary(var name: String, var id: String) : java.io.Serializable  {
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