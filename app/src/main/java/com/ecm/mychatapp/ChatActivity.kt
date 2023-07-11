package com.ecm.mychatapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatActivity : AppCompatActivity() {
    private var userId: String? = null
    private lateinit var senderId: String
    private lateinit var receiverId: String
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private lateinit var list: ArrayList<FriendlyMessage>
    private var mFirebaseDatabaseRef: DatabaseReference? = null
    private lateinit var mAuth: FirebaseAuth
    private var mFirebaseUser: FirebaseUser? = null

    private var mLinearLayoutManager: LinearLayoutManager? = null
    private var myChatAdapter: MyChatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        mAuth = FirebaseAuth.getInstance()

        var data = intent.extras
        if (data != null) {
            receiverId = intent.getStringExtra("userId")!!
            userId = intent.extras!!.getString("userId")
        }

        list = ArrayList()
        myChatAdapter = MyChatAdapter(this, userId, list, receiverId)

        mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager!!.stackFromEnd = true

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val actionBarView = inflater.inflate(R.layout.custom_bar_image, null)

        val customBarName = actionBarView.findViewById<TextView>(R.id.customBarName)
        val customBarCircleImage = actionBarView.findViewById<CircleImageView>(R.id.customBarCircleImage)

        val profileImgLink = intent.extras!!.getString("profile").toString()

        customBarName.text = intent.extras!!.getString("name")
        Picasso.get()
            .load(profileImgLink)
            .placeholder(R.drawable.profile_img)
            .into(customBarCircleImage)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.customView = actionBarView

        senderId = mAuth.currentUser!!.uid
        senderRoom = senderId + receiverId
        receiverRoom = receiverId + senderId

        val messageRecyclerView = findViewById<RecyclerView>(R.id.messageRecyclerView)
        messageRecyclerView.layoutManager = mLinearLayoutManager
        messageRecyclerView.adapter = myChatAdapter

        val sendButton = findViewById<Button>(R.id.sendButton)

        sendButton.setOnClickListener {
            val currentUsername = intent.extras!!.getString("name")
            val mCurrentUserId = mFirebaseUser!!.uid
            val messageEdt = findViewById<EditText>(R.id.messageEdt)

            val friendlyMessage = FriendlyMessage(
                mCurrentUserId,
                messageEdt.text.toString().trim(),
                currentUsername.toString().trim()
            )

            mFirebaseDatabaseRef!!.child("messages").child(senderRoom)
                .push().setValue(friendlyMessage)
            mFirebaseDatabaseRef!!.child("messages").child(receiverRoom)
                .push().setValue(friendlyMessage)

            messageEdt.setText("")
        }

        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().reference
        val senderMessagesRef = mFirebaseDatabaseRef?.child("messages")?.child(senderRoom)
        senderMessagesRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messagesList = ArrayList<FriendlyMessage>()
                for (snapshot in dataSnapshot.children) {
                    val message = snapshot.getValue(FriendlyMessage::class.java)
                    message?.let { messagesList.add(it) }
                }
                myChatAdapter?.updateData(messagesList)
                messageRecyclerView.scrollToPosition(messagesList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        val receiverMessagesRef = mFirebaseDatabaseRef?.child("messages")?.child(receiverRoom)
        receiverMessagesRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messagesList = ArrayList<FriendlyMessage>()
                for (snapshot in dataSnapshot.children) {
                    val message = snapshot.getValue(FriendlyMessage::class.java)
                    message?.let { messagesList.add(it) }
                }
                myChatAdapter?.updateData(messagesList)
                messageRecyclerView.scrollToPosition(messagesList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}


//class ChatActivity : AppCompatActivity() {
//    private var userId: String? = null
//    private lateinit var senderId: String
//    private lateinit var receiverId: String
//    private lateinit var senderRoom: String
//    private lateinit var receiverRoom: String
//    private lateinit var list: ArrayList<FriendlyMessage>
//    private var mFirebaseDatabaseRef: DatabaseReference? = null
//    private lateinit var mAuth: FirebaseAuth
//    private var mFirebaseUser: FirebaseUser? = null
//
//    private var mLinearLayoutManager: LinearLayoutManager? = null
//    private var myChatAdapter: MyChatAdapter? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_chat)
//
//        mFirebaseUser = FirebaseAuth.getInstance().currentUser
//        mAuth = FirebaseAuth.getInstance() // Initialize the mAuth variable
//
//        val data = intent.extras
//        if (data != null) {
//            receiverId = intent.getStringExtra("userId")!!
//            userId = intent.extras!!.getString("userId")
//            val profileImgLink = intent.extras!!.get("profile").toString()
//        }
//
//        list = ArrayList() // Initialize the list here
//
//        myChatAdapter = MyChatAdapter(this, userId, list, receiverId)
//
//        mLinearLayoutManager = LinearLayoutManager(this)
//        mLinearLayoutManager!!.stackFromEnd = true
//
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowCustomEnabled(true)
//
//        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//
//        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().reference
//
//        val messageRecyclerView = findViewById<RecyclerView>(R.id.messageRecyclerView)
//        messageRecyclerView.layoutManager = mLinearLayoutManager
//        messageRecyclerView.adapter = myChatAdapter
//
//        senderId = mAuth.currentUser!!.uid
//
//        senderRoom = senderId + receiverId
//        receiverRoom = receiverId + senderId
//
//        val sendButton = findViewById<Button>(R.id.sendButton)
//
//        sendButton.setOnClickListener {
//            if (intent!!.extras!!.getString("name").toString() != null) {
//                val currentUsername = intent!!.extras!!.get("name")
//                val mCurrentUserId = mFirebaseUser!!.uid
//                val messageEdt = findViewById<EditText>(R.id.messageEdt)
//
//                val friendlyMessage = FriendlyMessage(
//                    mCurrentUserId,
//                    messageEdt.text.toString().trim(),
//                    currentUsername.toString().trim()
//                )
//
//                mFirebaseDatabaseRef!!.child("messages").child(senderRoom)
//                    .push().setValue(friendlyMessage)
//
//                messageEdt.setText("")
//            }
//        }
//
//        val messagesRef = mFirebaseDatabaseRef?.child("messages")?.child(senderRoom)
//        messagesRef?.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val messagesList = ArrayList<FriendlyMessage>()
//                for (snapshot in dataSnapshot.children) {
//                    val message = snapshot.getValue(FriendlyMessage::class.java)
//                    message?.let { messagesList.add(it) }
//                }
//                myChatAdapter?.updateData(messagesList)
//                messageRecyclerView.scrollToPosition(messagesList.size - 1)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle error
//            }
//        })
//    }
//}
//






