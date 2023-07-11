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

class LoginActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mDatabase: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        var loginButton = findViewById<Button>(R.id.loginButtonId)
        loginButton.setOnClickListener {
            var email = findViewById<EditText>(R.id.loginEmailEt).text.toString().trim()
            var password = findViewById<EditText>(R.id.loginPasswordEt).text.toString().trim()

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                loginUser(email, password)
            }else{
                Toast.makeText(this, "Sorry, Login Failed", Toast.LENGTH_LONG).show()
            }

        }
    }

   private fun loginUser(email: String, password: String) {
       mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener {
           task: Task<AuthResult> ->
           if(task.isSuccessful){
               var userName = email.split("@")[0]
               var dashboardIntent = Intent(this, DashboardActivity::class.java)
               dashboardIntent.putExtra("name", userName)
               startActivity(dashboardIntent)
               finish()

           }else{
               Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
           }
       }

    }
}