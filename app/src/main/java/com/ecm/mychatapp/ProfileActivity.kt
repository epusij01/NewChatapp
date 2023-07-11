package com.ecm.mychatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    var mCurrentUser: FirebaseUser? = null
    var mUsersDatabase: DatabaseReference? = null
    var userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar!!.title = "Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (intent.extras != null){
            userId = intent.getStringExtra("userId").toString()
            mCurrentUser = FirebaseAuth.getInstance().currentUser
            mUsersDatabase = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)

            setUpProfile()
        }
    }

    private fun setUpProfile() {
        mUsersDatabase!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
              var displayName = snapshot.child("display_name").value.toString()
                var status = snapshot.child("status").value.toString()
                var image = snapshot.child("thumb_image").value.toString()
                var profileName = findViewById<TextView>(R.id.profileName)
                var profileStatus = findViewById<TextView>(R.id.profileStatus)
                var profileImage = findViewById<ImageView>(R.id.profilePicture)
                profileName.text = displayName
                profileStatus.text = status
                Picasso.get().load(image).into(profileImage)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}