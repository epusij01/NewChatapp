package com.ecm.mychatapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class CreateAccountActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    var mDatabase: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        var accountCreateButton = findViewById<Button>(R.id.accountCreateActBtn)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference


        accountCreateButton.setOnClickListener {
            var email = findViewById<EditText>(R.id.accountEmailEt).text.toString().trim()
            var password = findViewById<EditText>(R.id.accountPasswordEt).text.toString().trim()
            var displayName = findViewById<EditText>(R.id.accountDisplayNameEt).text.toString().trim()

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(
                    displayName)) {
                createAccount(email, password, displayName)
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_LONG).show()
            }

        }

        mAuth = FirebaseAuth.getInstance()
    }

    fun createAccount(email: String, password: String, displayName: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->

                if (task.isSuccessful) {
                    var currentUser = mAuth!!.currentUser
                    if (currentUser != null) {
                        var userId = currentUser.uid
                        mDatabase = FirebaseDatabase.getInstance().reference.child("users").child(userId)

                        var userObject = HashMap<String, String>()
                        userObject.put("display_name", displayName)
                        userObject.put("status", "Hello there....")
                        userObject.put("image", "default")
                        userObject.put("thumb_image", "default")

                        mDatabase!!.setValue(userObject).addOnCompleteListener { task: Task<Void> ->
                            if (task.isSuccessful) {
                               var dashboardIntent = Intent(this, DashboardActivity::class.java)
                                dashboardIntent.putExtra("name", displayName)
                                startActivity(dashboardIntent)
                                finish()
                            } else {
                                Toast.makeText(applicationContext, "User Not Created", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

            }
    }
}

