package com.example.travelapp.itineraries

class Itinerary(var name: String) : java.io.Serializable  {
    val steps: ArrayList<Step> = ArrayList()

    fun calculateItineraryPrice(): Float {
        var totalPrice = 0f
        for (step in steps) {
            totalPrice += step.price
        }
        return totalPrice
    }
}