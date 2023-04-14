package com.example.mapstemplate.ui.contacts

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.activities.ItineraryActivity
import com.example.mapstemplate.databinding.FragmentSlideshowBinding
import com.example.travelapp.adapters.ItineraryListAdapter
import com.example.travelapp.itineraries.Itinerary

class ContactsFragment : Fragment() {
    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    lateinit var listViewItinerary: ListView
    lateinit var listViewItineraryFollowing: ListView
    lateinit var itineraryListAdapter: ItineraryListAdapter
    lateinit var itineraryListAdapterFollowing: ItineraryListAdapter

    val itineraryList: ArrayList<Itinerary> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        itineraryList.addAll(HomeActivity.globalItineraryList)

        listViewItinerary = root.findViewById(R.id.likedItineraries)
        listViewItineraryFollowing = root.findViewById(R.id.followingItineraries)

        setupItineraryListView()
        setupItineraryListViewFollowing()

        val searchButton = root.findViewById<Button>(R.id.btnSearch)
        val inputField = root.findViewById<EditText>(R.id.itineraryName)
        var enteredSearch = ""

        /*searchButton.setOnClickListener {
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

        }*/
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

    fun setupItineraryListViewFollowing() {
        itineraryListAdapterFollowing = ItineraryListAdapter(requireContext(), itineraryList) { position ->
            val intent = Intent(context, ItineraryActivity::class.java)
            intent.putExtra("itinerary_index", position)
            intent.putExtra("is_global", true)
            startActivity(intent)
        }

        listViewItineraryFollowing.isClickable = true
        listViewItineraryFollowing.adapter = itineraryListAdapter
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
/*package com.example.mapstemplate.ui.contacts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapstemplate.ContactsAdapter
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.User
import com.example.mapstemplate.activities.AddItineraryActivity
import com.example.mapstemplate.databinding.FragmentSlideshowBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ContactsFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!


    private lateinit var friendsList : ArrayList<User>
    private lateinit var friendsNames : ArrayList<String>
    private lateinit var localUser : User

    lateinit var createButton: LinearLayout

    private val thisName = "contacts fragment"

    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth : FirebaseAuth


    //Declaration of recycler view variable and recycler view adapter for contacts page
    private lateinit var contactsRecycler : RecyclerView
    private lateinit var contactsAdapter : ContactsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //declaration of view model variable and assignment to view-model

        //val tempString : String? = homeActivity.tempString


        friendsList = ArrayList()
        friendsNames = ArrayList()

        mAuth = FirebaseAuth.getInstance()

        val contactsViewModel =
            ViewModelProvider(this)[ContactsViewModel::class.java]



        //set root variable to fragment view
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // assign adapter class constructor to contactsAdapter, then make the contactsRecycler point
        // to the correct recycler view in the correct LinearLayoutManager, finally attach the
        // recycler view to the adapter

        createButton = root.findViewById(R.id.createButton)

        createButton.setOnClickListener{
            val intent = Intent(context, AddFriend::class.java)
            startActivity(intent)
        }

        //val homeActivity = activity as AddFriend
        //val tempString : String? = homeActivity.tempString

        var testUser : User
        testUser = User()
        testUser.email = "patrickfarmer09@outlook.ie"
        testUser.nick = "Patrick"
        friendsList.add(testUser)
        /*if (tempString != null) {
            friendsNames.add(tempString)
            Log.i("FriendsTag", "Patrick testing the string: $tempString")
        }*/




        contactsAdapter = ContactsAdapter(friendsList)
        contactsRecycler = root.findViewById(R.id.contactsRecycler)
        contactsRecycler.layoutManager = LinearLayoutManager(context)
        contactsRecycler.adapter = contactsAdapter

        mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("user").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //friendsList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if(mAuth.currentUser?.uid == currentUser?.uid) {
                        currentUser?.nick = "you"
                    }
                    friendsList.add(currentUser!!)
                    currentUser.nick?.let { friendsNames.add(it) }
                    Log.i("MyTag", "Adding user with email ${currentUser.email} to contacts")
                }
                contactsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


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
}*/