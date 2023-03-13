package com.example.mapstemplate.ui.itinerary

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.activities.AddItineraryActivity
import com.example.mapstemplate.activities.ItineraryActivity
import com.example.mapstemplate.databinding.FragmentItineraryBinding
import com.example.travelapp.adapters.ItineraryListAdapter
import com.example.travelapp.itineraries.StaticData

class ItineraryFragment : Fragment() {
    private var _binding: FragmentItineraryBinding? = null
    private val binding get() = _binding!!

    lateinit var listViewItinerary: ListView
    lateinit var createButton: LinearLayout
    lateinit var itineraryListAdapter: ItineraryListAdapter

    companion object {
        val itineraryList = HomeActivity.userItineraryList
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItineraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createButton = root.findViewById(R.id.createButton)
        listViewItinerary = root.findViewById(R.id.list_item)

        setupButtons()
        setupItineraryListView()

        return root
    }

    fun setupItineraryListView() {
        itineraryListAdapter = ItineraryListAdapter(requireContext(), itineraryList)
        listViewItinerary.isClickable = true
        listViewItinerary.adapter = itineraryListAdapter

        listViewItinerary.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(context, ItineraryActivity::class.java)
            intent.putExtra("itinerary_index", position)
            startActivity(intent)
        }
    }

    fun setupButtons() {
        createButton.setOnClickListener{
            val intent = Intent(context, AddItineraryActivity::class.java)
            startActivity(intent)
        }
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