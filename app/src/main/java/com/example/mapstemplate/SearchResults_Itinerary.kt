package com.example.mapstemplate

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


class SearchResultsItinerary : AppCompatActivity() {

    private lateinit var resultsRecycler : RecyclerView
    private lateinit var resultsAdapter : ResultsAdapter

    private lateinit var searchButton : Button
    private lateinit var searchContent : TextView

    private lateinit var results : ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results_itinerary)

        results = ArrayList()

        results.clear()

        searchButton = findViewById(R.id.search_button)
        searchContent = findViewById(R.id.searchText)

        searchButton.setOnClickListener{
            val place : String = searchContent.text.toString()
                searchPlace(place)
        }

        resultsAdapter = ResultsAdapter(results)
        resultsRecycler = findViewById(R.id.itinresultsRecycler)
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
                val key : String = "AIzaSyBn1QAii8KpmxExEE2WoN_89XMGhEhfx9Q"
                //key = getString(R.string.api_key)
                val urlStr : String = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=$name&key=$key"
                //https://maps.googleapis.com/maps/api/place/textsearch/json?query=Buttery&key=AIzaSyBn1QAii8KpmxExEE2WoN_89XMGhEhfx9Q
                val url : URL = URL(urlStr)
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

            Log.i("PlacesAPI", "====================================================")
            Log.i("PlacesAPI", "Name: $name")
            results.add(name)
        }
    }
}