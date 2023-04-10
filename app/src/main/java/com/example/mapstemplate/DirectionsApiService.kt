package com.example.mapstemplate
// Retrofit library
import com.example.mapstemplate.DirectionsApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


// declaration of the DirectionsApiService interface, which is used to define the methods for interacting with the Directions API using Retrofit.
interface DirectionsApiService {
// HTTP GET request
    
    @GET("maps/api/directions/json")
    suspend fun getDirections(
    //origin and destination parameters
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
        //declaration of the DirectionsApiService interface
    ): Response<DirectionsApiResponse>
}
