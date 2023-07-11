package com.ecm.mychatapp

import android.icu.lang.UCharacter.VerticalOrientation
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.io.path.fileVisitor


class UsersFragment : Fragment() {
    private lateinit var userRecycler: RecyclerView
    private lateinit var usersAdapter: UsersAdapter
    private var mUserDatabase: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        userRecycler = view.findViewById(R.id.userRecyclerViewId)
        userRecycler.layoutManager = linearLayoutManager
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("users")

        val options = FirebaseRecyclerOptions.Builder<Users>()
            .setQuery(mUserDatabase!!, Users::class.java)
            .build()

        usersAdapter = UsersAdapter(options, requireContext())
        userRecycler.adapter = usersAdapter
    }

    override fun onStart() {
        super.onStart()
        usersAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        usersAdapter.stopListening()
    }
}


//class UsersFragment : Fragment() {
//    private var mUserDatabase: DatabaseReference? = null
//    private lateinit var userRecycler: RecyclerView
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_users, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        mUserDatabase = FirebaseDatabase.getInstance().reference.child("users")
//        userRecycler = view.findViewById(R.id.userRecyclerViewId)
//        userRecycler.layoutManager = linearLayoutManager
//        userRecycler.setHasFixedSize(true)
//        // Initialize and set up your FirebaseRecyclerAdapter with the userRecycler
//        val usersAdapter = UsersAdapter(mUserDatabase!!, requireContext())
//        userRecycler.adapter = usersAdapter
//    }
//}
