package com.ecm.mychatapp

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UsersAdapter(options: FirebaseRecyclerOptions<Users>, private val context: Context) :
    FirebaseRecyclerAdapter<Users, UsersAdapter.ViewHolder>(options) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTxt: TextView = itemView.findViewById(R.id.userName)
        val userStatusTxt: TextView = itemView.findViewById(R.id.userStatus)
        val userProfilePic: CircleImageView = itemView.findViewById(R.id.usersProfile)

        // Bind view logic goes here
        fun bindView(user: Users, context: Context) {
            userNameTxt.text = user.display_name
            userStatusTxt.text = user.user_status

            val userProfileLink = user.thumb_image
            Picasso.get().load(userProfileLink)
                .placeholder(R.drawable.profile_img)
                .into(userProfilePic)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.users_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, user: Users) {
        holder.bindView(user, context)

        holder.itemView.setOnClickListener {
            // Item click logic goes here
            val userId = getRef(position).key

            val options = arrayOf("Open Profile", "Send Message")
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Select Options")
            builder.setItems(options) { _, i ->
                val userName = holder.userNameTxt.text
                val userStat = holder.userStatusTxt.text
                val profilePic = user.thumb_image

                if (i == 0) {
                    // Open user profile
                    val profileIntent = Intent(context, ProfileActivity::class.java)
                    profileIntent.putExtra("userId", userId)
                    context.startActivity(profileIntent)
                } else {
                    // Send Message/ChatActivity
                    val chatIntent = Intent(context, ChatActivity::class.java)
                    chatIntent.putExtra("userId", userId)
                    chatIntent.putExtra("name", userName)
                    chatIntent.putExtra("status", userStat)
                    chatIntent.putExtra("profile", profilePic)
                    context.startActivity(chatIntent)
                }
            }

            builder.show()
        }
    }
}


//class UsersAdapter(query: Query, context: Context) :
//    FirebaseRecyclerAdapter<Users, UsersAdapter.ViewHolder>(
//        FirebaseRecyclerOptions.Builder<Users>()
//            .setQuery(query, Users::class.java)
//            .build()
//    ) {
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        // Initialize your ViewHolder views here
//        var userNameTxt: String? = null
//        var userStatusTxt: String? = null
//        var userProfileLink: String? = null
//
//        fun bindViews(user: Users, context: Context){
//            var userName = itemView.findViewById<TextView>(R.id.userName)
//            var userStatus = itemView.findViewById<TextView>(R.id.userStatus)
//            var userProfilePic = itemView.findViewById<CircleImageView>(R.id.usersProfile)
//
//            userNameTxt = user.display_name.toString()
//            userStatusTxt = user.user_status.toString()
//            userProfileLink = user.thumb_image.toString()
//
//            userName.text = userNameTxt
//            userStatus.text = userStatusTxt
//            Picasso.get().load(userProfileLink)
//                .placeholder(R.drawable.profile_img)
//                .into(userProfilePic)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.users_row, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int, user: Users?) {
//        // Bind data to your ViewHolder views here
//        var userId = getRef(position).key
//        holder.bindViews(user!!, holder.itemView.context)
//    }
//}
