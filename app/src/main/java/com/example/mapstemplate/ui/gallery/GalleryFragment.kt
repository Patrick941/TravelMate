package com.example.mapstemplate.ui.gallery

import android.os.Bundle
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
import com.example.mapstemplate.RecommendationsAdapter
import com.example.mapstemplate.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var historyRecycler : RecyclerView
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        historyAdapter = HistoryAdapter()
        historyRecycler = root.findViewById(R.id.historyRecycler)
        historyRecycler.layoutManager = LinearLayoutManager(context)
        historyRecycler.adapter = historyAdapter

        val textView: TextView = binding.historyTV
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = "History"
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}