package com.example.mapstemplate.ui.contacts

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
import com.example.mapstemplate.ContactsAdapter
import com.example.mapstemplate.R
import com.example.mapstemplate.databinding.FragmentSlideshowBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    private val thisName = "contacts fragment"

    //Declaration of recycler view variable and recycler view adapter for contacts page
    private lateinit var contactsRecycler : RecyclerView
    private lateinit var contactsAdapter : ContactsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //declaration of view model variable and assignment to viewmodel
        val contactsViewModel =
            ViewModelProvider(this)[ContactsViewModel::class.java]

        //set root variable to fragment view
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // assign adapter class constructor to contactsAdapter, then make the contactsRecycler point
        // to the correct recycler view in the correct LinearLayoutManager, finally attach the
        // recycler view to the adapter
        contactsAdapter = ContactsAdapter()
        contactsRecycler = root.findViewById(R.id.contactsRecycler)
        contactsRecycler.layoutManager = LinearLayoutManager(context)
        contactsRecycler.adapter = contactsAdapter

        //Attach variable to correct textView
        val textView: TextView = binding.textSlideshow
        contactsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = "Contacts"
        }
        return root
    }


    //logging activity changes
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