package com.ecm.mychatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class StatusActivity : AppCompatActivity() {
    var mDataBase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        supportActionBar!!.title = "Status"
        var data = intent.extras
        if (data != null){
            var oldStatus = data.getString("status")
            var statusUpdateEt = findViewById<EditText>(R.id.statusUpdateEt)
//            statusUpdateEt.text = oldStatus.toString()
            statusUpdateEt.setText(oldStatus.toString())
        }
        if (data!!.equals(null)){
            var statusUpdateEt = findViewById<TextView>(R.id.statusUpdateEt)
            statusUpdateEt.setText("Enter your new status")

        }

        var statusBtn = findViewById<Button>(R.id.statusUpdateBtn)
        statusBtn.setOnClickListener {
            mCurrentUser = FirebaseAuth.getInstance().currentUser
            var userId = mCurrentUser!!.uid
            mDataBase = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            var status = findViewById<TextView>(R.id.statusUpdateEt).text.toString().trim()
            mDataBase!!.child("status").setValue(status).addOnCompleteListener {
                task : Task<Void> ->
                if(task.isSuccessful){
                    Toast.makeText(this, " Status updated successfully", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, SettingsActivity::class.java ))
                }else{
                    Toast.makeText(this, " Status Not updated ", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}