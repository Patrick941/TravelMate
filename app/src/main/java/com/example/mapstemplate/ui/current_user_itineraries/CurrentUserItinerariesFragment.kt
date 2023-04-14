package com.example.mapstemplate.ui.current_user_itineraries

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.User
import com.example.mapstemplate.activities.ItineraryActivity
import com.example.mapstemplate.databinding.FragmentCurrentUserItinerariesBinding
import com.example.mapstemplate.ui.contacts.AddFriend
import com.example.travelapp.adapters.ItineraryListAdapter
import com.example.travelapp.itineraries.Itinerary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class  CurrentUserItinerariesFragment : Fragment() {
    private var _binding: FragmentCurrentUserItinerariesBinding? = null
    // private var _binding: NavBarHomeBinding? = null
    private val binding get() = _binding!!



    val itineraryList: ArrayList<Itinerary> = ArrayList()

    // lateinit var createButton: LinearLayout

    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth : FirebaseAuth
    // lateinit var testButton: BottomNavigationView
    lateinit var itineraryListAdapter: ItineraryListAdapter


    private lateinit var actualFriends : ArrayList<User>
    private lateinit var actualFriendsNames : ArrayList<String>
    private lateinit var friendsList : ArrayList<User>
    private lateinit var friendsNames : ArrayList<String>
    private lateinit var friendsToPrint : ArrayList<String>

    private var amountOfFriends: Int = 0


    lateinit var listViewItinerary: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        amountOfFriends = 0

        _binding = FragmentCurrentUserItinerariesBinding.inflate(inflater, container, false)
        // _binding = NavBarHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // val rb = root.findViewById<View>(R.id.ratingBar) // idk what I am doing ??!!


        friendsList = ArrayList()
        friendsNames = ArrayList()
        actualFriendsNames = ArrayList()
        actualFriends = ArrayList()
        friendsToPrint = ArrayList()

//        add = root.findViewById(R.id.add)
        // createButton = root.findViewById(R.id.createButton)

        listViewItinerary = root.findViewById(R.id.list_item)

        //val view = inflater.inflate(R.layout.fragment_current_user_itineraries, container, false)
        val view = binding.root

        val followingText = binding.followingLabel
        val followingCount = binding.followingCount
        val followersCount = binding.followersCount


        followingText.setOnClickListener {
            Log.i("ProfileTag", "Followers Clicked")
            val intent = Intent(context, AddFriend::class.java)
            startActivity(intent)
        }

        followingCount.setOnClickListener {
            Log.i("ProfileTag", "Followers Clicked")
            val intent = Intent(context, AddFriend::class.java)
            startActivity(intent)
        }



        itineraryList.addAll(HomeActivity.globalItineraryList)

        mAuth = FirebaseAuth.getInstance()

        mDbRef = FirebaseDatabase.getInstance().reference

        val userId = mAuth.currentUser?.uid

        mDbRef.child("user").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itineraryList.addAll(HomeActivity.globalItineraryList)
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if(mAuth.currentUser?.uid == currentUser?.uid) {
                        currentUser?.nick = "you"
                    }
                    friendsList.add(currentUser!!)
                    currentUser.nick?.let { friendsNames.add(it) }
                    Log.i("MyTag", "Adding user with email ${currentUser.email} to contacts")
                }
                // Move the second query here, inside the onDataChange callback of the first query
                mDbRef.child("user").child(userId!!).child("Friends").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (friendSnapshot in snapshot.children) {
                            val friendEmail = friendSnapshot.getValue(String::class.java)
                            actualFriendsNames.add(friendEmail!!)
                            Log.i("MyTag", "Adding friend with email $friendEmail to contacts")

                            // Check if the email is in the friends list
                            for (friend in friendsList) {
                                Log.i("MyTag", "Testing for ${friend.nick}")
                                if (friend.email == friendEmail) {
                                    // Add the friend's nickname to a new array
                                    actualFriends.add(friend)
                                    amountOfFriends = amountOfFriends + 1
                                    //logItineraryEmails(friend)
                                    Log.i("MyTag", "Adding friend with nickname ${friend.nick} to actualFriends")
                                    break
                                }
                            }
                            followingCount.text = "$amountOfFriends"
                            followersCount.text = "$amountOfFriends"
                        }
                        //contactsAdapter.notifyDataSetChanged() // Notify the adapter that the data has changed
                        Log.i("MyTag", "Testing the order in which things are executed")

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("MyTag", "Failed to read value.", error.toException())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        // setupButtons()
//        setupNewButtons()
        setupItineraryListView()

        return root
    }

    fun setupItineraryListView() {
        itineraryListAdapter = ItineraryListAdapter(requireContext(), HomeActivity.currentUserItineraryList) { position ->
            val intent = Intent(requireContext(), ItineraryActivity::class.java)
            intent.putExtra("itinerary_index", position)
            intent.putExtra("is_global", false)
            startActivity(intent)
        }

        listViewItinerary.isClickable = true
        listViewItinerary.adapter = itineraryListAdapter
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