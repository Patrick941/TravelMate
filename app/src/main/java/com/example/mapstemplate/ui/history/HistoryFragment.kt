package com.example.mapstemplate.ui.history

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
import com.example.mapstemplate.HistoryAdapter
import com.example.mapstemplate.R
import com.example.mapstemplate.databinding.FragmentGalleryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val thisName = "history fragment"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Declaration of recycler view variable and recycler view adapter for history page
    private lateinit var historyRecycler : RecyclerView
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //declaration of view model variable and assignment to viewmodel
        val galleryViewModel =
            ViewModelProvider(this).get(HistoryViewModel::class.java)

        //set root variable to fragment view
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // assign adapter class constructor to historyAdapter, then make the historyRecycler point
        // to the correct recycler view in the correct LinearLayoutManager, finally attach the
        // recycler view to the adapter
        historyAdapter = HistoryAdapter()
        historyRecycler = root.findViewById(R.id.historyRecycler)
        historyRecycler.layoutManager = LinearLayoutManager(context)
        historyRecycler.adapter = historyAdapter

        //Attach variable to correct textView
        val textView: TextView = binding.historyTV
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = "History"
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
        Log.i("MyTag", "destroying view for $thisName")
        _binding = null
    }
}