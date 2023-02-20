package com.example.mapstemplate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapstemplate.NotificationsAdapter
import com.example.mapstemplate.R
import com.example.mapstemplate.RecommendationsAdapter
import com.example.mapstemplate.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val thisName = "Home Fragment"

    private lateinit var recommendationsRecycler : RecyclerView
    private lateinit var recommendationsAdapter : RecommendationsAdapter

    private lateinit var notificationsRecycler : RecyclerView
    private lateinit var notificationsAdapter : NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*recommendationsAdapter = RecommendationsAdapter(R.layout.fragment_home)
        recommendationsRecycler = root.findViewById(R.id.LocationsRecycler)
        recommendationsRecycler.layoutManager = LinearLayoutManager(context)
        recommendationsRecycler.adapter = recommendationsAdapter

        notificationsAdapter = NotificationsAdapter(R.layout.fragment_home)
        notificationsRecycler = root.findViewById(R.id.NotificationsRecycler)
        notificationsRecycler.layoutManager = LinearLayoutManager(context)
        notificationsRecycler.adapter = notificationsAdapter*/

        val locationView: TextView = binding.BasedOnLocation
        homeViewModel.text.observe(viewLifecycleOwner) {
            locationView.text = "Recommended based on location"
        }

        val notificationsView: TextView = binding.Notifications
        homeViewModel.text.observe(viewLifecycleOwner) {
            notificationsView.text = "Recent Notifications"
        }
        return root
    }

    override fun onPause(){
        super.onPause()
        Log.i("MyTag", "pausing $thisName")
    }
    override fun onResume(){
        super.onResume()
        Log.i("MyTag", "resuming $thisName")
    }
    override fun onStart(){
        super.onStart()
        Log.i("MyTag", "starting $thisName")
    }
    override fun onStop(){
        super.onStop()
        Log.i("MyTag", "stopping $thisName")
    }
    override fun onDestroy(){
        super.onDestroy()
        Log.i("MyTag", "destroying $thisName")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}