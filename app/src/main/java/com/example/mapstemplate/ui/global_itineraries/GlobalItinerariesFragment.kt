package com.example.mapstemplate.ui.global_itineraries

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.activities.AddItineraryActivity
import com.example.mapstemplate.activities.ItineraryActivity
import com.example.mapstemplate.databinding.FragmentGlobalItinerariesBinding
import com.example.travelapp.adapters.ItineraryListAdapter
import com.example.travelapp.itineraries.Itinerary

class GlobalItinerariesFragment : Fragment() {
    private var _binding: FragmentGlobalItinerariesBinding? = null
    private val binding get() = _binding!!

    lateinit var listViewItinerary: ListView
    lateinit var itineraryListAdapter: ItineraryListAdapter

    val itineraryList: ArrayList<Itinerary> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGlobalItinerariesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        itineraryList.clear()
        itineraryList.addAll(HomeActivity.globalItineraryList)

        listViewItinerary = root.findViewById(R.id.global_itineraries_list_item)

        setupItineraryListView()

        val searchButton = root.findViewById<Button>(R.id.btnSearch)
        val inputField = root.findViewById<EditText>(R.id.itineraryName)
        var enteredSearch = ""

        searchButton.setOnClickListener {
            val newItineraryList = ArrayList<Itinerary>()
            enteredSearch = inputField.text.toString()
            for (i in HomeActivity.globalItineraryList) {
                if (i.name.contains(enteredSearch, true)) {
                    newItineraryList.add(i)
                } else {
                    Toast.makeText(
                        context,
                        "Please, enter an itinerary name",
                        Toast.LENGTH_SHORT).show()
                }
            }
            itineraryList.clear()
            itineraryList.addAll(newItineraryList)
            itineraryListAdapter.notifyDataSetChanged()

        }
        return root
    }

    fun setupItineraryListView() {
        itineraryListAdapter = ItineraryListAdapter(requireContext(), itineraryList) { position ->
            val intent = Intent(context, ItineraryActivity::class.java)
            intent.putExtra("itinerary_index", position)
            intent.putExtra("is_global", true)
            startActivity(intent)
        }

        listViewItinerary.isClickable = true
        listViewItinerary.adapter = itineraryListAdapter
    }




    override fun onResume() {
        super.onResume()
        // update the data in the listView
        itineraryListAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}