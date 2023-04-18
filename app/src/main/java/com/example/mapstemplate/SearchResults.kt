package com.example.mapstemplate

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


class SearchResults : AppCompatActivity() {

    private lateinit var resultsRecycler : RecyclerView
    private lateinit var resultsAdapter : ResultsAdapter

    private lateinit var results : ArrayList<String>
    private lateinit var resultCordinates : ArrayList<LatLng>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        results = ArrayList()
        resultCordinates = ArrayList()

        results.clear()

        val intent = getIntent()
        val message = intent.getStringExtra("searchText")

        if (message != null) {
            //results.add(message)
            searchPlace(message)
        }

        resultsAdapter = ResultsAdapter(results, resultCordinates)
        resultsRecycler = findViewById(R.id.resultsRecycler)
        resultsRecycler.layoutManager = LinearLayoutManager(this)
        resultsRecycler.adapter = resultsAdapter
    }

    private fun searchPlace(name: String){
        var result = ""

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            //your codes here
            try{
                var key : String = "AIzaSyBn1QAii8KpmxExEE2WoN_89XMGhEhfx9Q"
                //key = getString(R.string.api_key)
                var urlStr : String = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=$name&key=$key"
                //https://maps.googleapis.com/maps/api/place/textsearch/json?query=Buttery&key=AIzaSyBn1QAii8KpmxExEE2WoN_89XMGhEhfx9Q
                var url : URL = URL(urlStr)
                println(url)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("Content-Type", "application/json")
                connection.requestMethod = "GET"
                connection.doInput = true
                val br = connection.inputStream.bufferedReader()
                result = br.use {br.readText()}
                //result = br.use {br.r}
                parseJsonSearch(result)
                connection.disconnect()
            } catch(e:Exception){
                e.printStackTrace()
                result = "error"
            }
            //Log.i("PlacesAPIExtra", result)
        }
    }

    private fun parseJsonSearch(jsonString: String) {
        val jsonObject = JSONObject(jsonString)
        val resultsArray: JSONArray = jsonObject.getJSONArray("results")

        for (i in 0 until resultsArray.length()) {
            val resultObject = resultsArray.getJSONObject(i)
            val name = resultObject.getString("name")
            val lat = resultObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
            val lng = resultObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng")


            Log.i("PlacesAPI", "====================================================")
            Log.i("PlacesAPI", "Name: $name")
            Log.i("PlacesAPI", "Latitude: $lat")
            Log.i("PlacesAPI", "Longitude: $lng")
            results.add(name)
            resultCordinates.add(LatLng(lat,lng))
        }
    }
}