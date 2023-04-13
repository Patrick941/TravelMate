package com.example.mapstemplate.ui.current_user_itineraries

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.MapsActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.activities.AddItineraryActivity
import com.example.mapstemplate.activities.ItineraryActivity
import com.example.mapstemplate.databinding.FragmentCurrentUserItinerariesBinding
import com.example.mapstemplate.profile
import com.example.mapstemplate.databinding.NavBarHomeBinding
import com.example.mapstemplate.ui.home.HomeFragment
import com.example.travelapp.adapters.ItineraryListAdapter
import com.example.mapstemplate.LikesPage

class CurrentUserItinerariesFragment : Fragment() {
    private var _binding: FragmentCurrentUserItinerariesBinding? = null
    // private var _binding: NavBarHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var listViewItinerary: ListView
    // lateinit var createButton: LinearLayout
    // lateinit var testButton: BottomNavigationView
    lateinit var itineraryListAdapter: ItineraryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentUserItinerariesBinding.inflate(inflater, container, false)
        // _binding = NavBarHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // val rb = root.findViewById<View>(R.id.ratingBar) // idk what I am doing ??!!

//        add = root.findViewById(R.id.add)
        // createButton = root.findViewById(R.id.createButton)

        listViewItinerary = root.findViewById(R.id.list_item)

        // setupButtons()
//        setupNewButtons()
        setupItineraryListView()

        return root
    }

    fun setupItineraryListView() {
        itineraryListAdapter = ItineraryListAdapter(requireContext(), HomeActivity.currentUserItineraryList)
        listViewItinerary.isClickable = true
        listViewItinerary.adapter = itineraryListAdapter

        listViewItinerary.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(context, ItineraryActivity::class.java)
            intent.putExtra("itinerary_index", position)
            intent.putExtra("is_global", false)
            startActivity(intent)
        }
    }

/*    fun setupButtons() {
        createButton.setOnClickListener{
            val intent = Intent(context, AddItineraryActivity::class.java)
            startActivity(intent)
        }
    }*/

//    private fun setupNewButtons() {
//        binding.bottomNavView.setOnItemReselectedListener {
//            when(it.itemId) {
//                R.id.test_home -> replaceActivity(HomeActivity())
//                R.id.test_map -> replaceActivity(MapsActivity())
//                R.id.test_add -> replaceActivity(AddItineraryActivity())
//                R.id.test_rating -> replaceActivity(LikesPage())
//                R.id.test_profile -> replaceActivity(profile())
//                else -> {
//                }
//            }
//            true
//        }
//    }
    override fun onResume() {
        super.onResume()
        // update the data in the listView
        itineraryListAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun replaceActivity(activity: Activity) {
        val intent = Intent(context, activity::class.java)
        startActivity(intent)
    }

    private fun replaceFragment(fragment: Fragment) {
        // val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }


}