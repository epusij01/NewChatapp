package com.ecm.mychatapp

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class MyChatAdapter(
    private val context: Context,
    private var userId: String?,
    private var list: ArrayList<FriendlyMessage>,
    private var receiverId: String?
) : RecyclerView.Adapter<MyChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friendlyMessage = list[position]
        holder.bindView(friendlyMessage)

        holder.itemView.setOnLongClickListener {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Delete")
            alertDialog.setMessage("Are you sure you want to delete the message?")
            alertDialog.setPositiveButton("Yes") { _, _ ->
                val database = FirebaseDatabase.getInstance()
                val messageId = friendlyMessage.id

                val senderRoom = FirebaseAuth.getInstance().uid + receiverId
                val receiverRoom = receiverId + FirebaseAuth.getInstance().uid

                // Remove the message from the sender's room
                database.reference.child("messages").child(senderRoom).child(messageId!!)
                    .removeValue()
                    .addOnSuccessListener {
                        // Remove the message from the receiver's room
                        database.reference.child("messages").child(receiverRoom).child(messageId)
                            .removeValue()
                            .addOnSuccessListener {
                                // Message deletion successful
                                Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show()
                                list.removeAt(holder.adapterPosition)
                                notifyItemRemoved(holder.adapterPosition)
                                notifyItemRangeChanged(holder.adapterPosition, list.size)
                            }
                            .addOnFailureListener { exception ->
                                // Handle deletion failure
                                Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show()
                                Log.e(TAG, "Failed to delete message: ${exception.message}")
                            }
                    }
                    .addOnFailureListener { exception ->
                        // Handle deletion failure
                        Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Failed to delete message: ${exception.message}")
                    }
            }
            alertDialog.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
            true
        }

        val mFirebaseUser = FirebaseAuth.getInstance().currentUser
        val mFirebaseDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference

        if (friendlyMessage.id == mFirebaseUser!!.uid) {
            holder.cardView.setCardBackgroundColor(Color.WHITE)
            holder.profileImageViewRight.visibility = View.VISIBLE
            holder.profileImageView.visibility = View.GONE
            holder.messageTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            holder.messengerTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.END

            mFirebaseDatabaseRef.child("users")
                .child(mFirebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(data: DataSnapshot) {
                        val imageUrl = data.child("thumb_image").value.toString()
                        val displayName = data.child("display_name").value

                        holder.messengerTextView.text = "I wrote..."

                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.profile_img)
                            .into(holder.profileImageViewRight)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        } else {
            holder.cardView.setCardBackgroundColor(Color.GRAY)
            holder.profileImageViewRight.visibility = View.GONE
            holder.profileImageView.visibility = View.VISIBLE
            holder.messageTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            holder.messengerTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.START

            mFirebaseDatabaseRef.child("users")
                .child(userId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(data: DataSnapshot) {
                        val imageUrl = data.child("thumb_image").value.toString()
                        val displayName = data.child("display_name").value.toString()

                        holder.messengerTextView.text = "$displayName wrote..."

                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.profile_img)
                            .into(holder.profileImageView)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(newList: ArrayList<FriendlyMessage>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextview)
        val cardView = itemView.findViewById<CardView>(R.id.cardViewId)
        val messengerTextView: TextView = itemView.findViewById(R.id.messengerTextview)
        val profileImageView: CircleImageView = itemView.findViewById(R.id.messengerImageView)
        val profileImageViewRight: CircleImageView = itemView.findViewById(R.id.messengerImageViewRight)

        fun bindView(friendlyMessage: FriendlyMessage) {
            messengerTextView.text = friendlyMessage.name
            messageTextView.text = friendlyMessage.text
        }
    }
}


//class MyChatAdapter(
//    private val context: Context,
//    private var userId: String?,
//    private var list: ArrayList<FriendlyMessage>,
//    private var receiverId: String?
//) : RecyclerView.Adapter<MyChatAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val friendlyMessage = list[position]
//        holder.bindView(friendlyMessage)
//// Delete message
//        holder.itemView.setOnLongClickListener {
//            val alertDialog = AlertDialog.Builder(context)
//            alertDialog.setTitle("Delete")
//            alertDialog.setMessage("Are you sure you want to delete the message?")
//            alertDialog.setPositiveButton("Yes") { _, _ ->
//                val database = FirebaseDatabase.getInstance()
//
//                val messageId = friendlyMessage.id
//                val senderRoom = FirebaseAuth.getInstance().uid + receiverId
//                database.reference.child("messages")
//                    .child(messageId!!).child("text")
//                    .setValue(null)
//            }
//            alertDialog.setNegativeButton("No") { dialog, _ ->
//                dialog.dismiss()
//            }
//            alertDialog.show()
//            true
//        }
//        // delete user ^||^
//
//        val mFirebaseUser = FirebaseAuth.getInstance().currentUser
//        val mFirebaseDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference
//
//        if (friendlyMessage.id == mFirebaseUser!!.uid) {
//            holder.profileImageViewRight.visibility = View.VISIBLE
//            holder.profileImageView.visibility = View.GONE
//            holder.messageTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.END
//            holder.messengerTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.END
//
//            mFirebaseDatabaseRef.child("users")
//                .child(mFirebaseUser.uid)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(data: DataSnapshot) {
//                        val imageUrl = data.child("thumb_image").value.toString()
//                        val displayName = data.child("display_name").value
//
//                        holder.messengerTextView.text = "I wrote..."
//
//                        Picasso.get()
//                            .load(imageUrl)
//                            .placeholder(R.drawable.profile_img)
//                            .into(holder.profileImageViewRight)
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        // Handle error
//                    }
//                })
//        } else {
//            holder.profileImageViewRight.visibility = View.GONE
//            holder.profileImageView.visibility = View.VISIBLE
//            holder.messageTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.START
//            holder.messengerTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.START
//
//            mFirebaseDatabaseRef.child("users")
//                .child(userId!!)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(data: DataSnapshot) {
//                        val imageUrl = data.child("thumb_image").value.toString()
//                        val displayName = data.child("display_name").value.toString()
//
//                        holder.messengerTextView.text = "$displayName wrote..."
//
//                        Picasso.get()
//                            .load(imageUrl)
//                            .placeholder(R.drawable.profile_img)
//                            .into(holder.profileImageView)
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        // Handle error
//                    }
//                })
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return list.size
//    }
//
//    fun updateData(newList: ArrayList<FriendlyMessage>) {
//        list.clear()
//        list.addAll(newList)
//        notifyDataSetChanged()
//    }
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val messageTextView: TextView = itemView.findViewById(R.id.messageTextview)
//        val messengerTextView: TextView = itemView.findViewById(R.id.messengerTextview)
//        val profileImageView: CircleImageView = itemView.findViewById(R.id.messengerImageView)
//        val profileImageViewRight: CircleImageView = itemView.findViewById(R.id.messengerImageViewRight)
//
//        fun bindView(friendlyMessage: FriendlyMessage) {
//            messengerTextView.text = friendlyMessage.name
//            messageTextView.text = friendlyMessage.text
//        }
//    }
//}
//
//
//

//class MyChatAdapter(
//    private val context: Context,
//    private var userId: String?,
//    private var list: ArrayList<FriendlyMessage>,
//    private var receiverId: String?
//) : RecyclerView.Adapter<MyChatAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bindView(list[position])
//        val mFirebaseUser = FirebaseAuth.getInstance().currentUser
//
//        val mFirebaseDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference
//        val model = list[position]
//        if (model.id == mFirebaseUser!!.uid) {
//            holder.profileImageViewRight.visibility = View.VISIBLE
//            holder.profileImageView.visibility = View.GONE
//            holder.messageTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.END
//            holder.messengerTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.END
//
//            // Get imageUrl for me
//            mFirebaseDatabaseRef.child("users")
//                .child(mFirebaseUser.uid)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(data: DataSnapshot) {
//                        val imageUrl = data.child("thumb_image").value.toString()
//                        val displayName = data.child("display_name").value
//
//                        holder.messengerTextView.text = "I wrote..."
//
//                        Picasso.get()
//                            .load(imageUrl)
//                            .placeholder(R.drawable.profile_img)
//                            .into(holder.profileImageViewRight)
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        // Handle error
//                    }
//                })
//        } else {
//            // The other person, show imageview to the left side
//            holder.profileImageViewRight.visibility = View.GONE
//            holder.profileImageView.visibility = View.VISIBLE
//            holder.messageTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.START
//            holder.messengerTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.START
//
//            // Get imageUrl for the other person
//            mFirebaseDatabaseRef.child("users")
//                .child(userId!!)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(data: DataSnapshot) {
//                        val imageUrl = data.child("thumb_image").value.toString()
//                        val displayName = data.child("display_name").value.toString()
//
//                        holder.messengerTextView.text = "$displayName wrote..."
//
//                        Picasso.get()
//                            .load(imageUrl)
//                            .placeholder(R.drawable.profile_img)
//                            .into(holder.profileImageView)
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        // Handle error
//                    }
//                })
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return list.size
//    }
//
//    fun updateData(newList: ArrayList<FriendlyMessage>) {
//        list = newList
//        notifyDataSetChanged()
//    }
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val messageTextView: TextView = itemView.findViewById(R.id.messageTextview)
//        val messengerTextView: TextView = itemView.findViewById(R.id.messengerTextview)
//        val profileImageView: CircleImageView = itemView.findViewById(R.id.messengerImageView)
//        val profileImageViewRight: CircleImageView = itemView.findViewById(R.id.messengerImageViewRight)
//
//        fun bindView(friendlyMessage: FriendlyMessage) {
//            messengerTextView.text = friendlyMessage.name
//            messageTextView.text = friendlyMessage.text
//        }
//    }
//}



