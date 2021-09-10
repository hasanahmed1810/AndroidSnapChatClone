package com.hasan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        auth = Firebase.auth

        if (auth.currentUser != null){
            logIn()
            Toast.makeText(this, "You Logged In", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun goClicked(view: View){
        if (emailEditText?.text.toString() != "" && passwordEditText?.text.toString() != ""){
            auth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        logIn()
                        Toast.makeText(this, "You Logged In", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        auth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    Firebase.database.reference.child("users").child(task.result!!.user!!.uid).child("email").setValue(emailEditText?.text.toString())
                                    logIn()
                                    Toast.makeText(this, "New Account Created", Toast.LENGTH_SHORT).show()
                                }
                                else {
                                    Toast.makeText(this, "Login Failed, Try Again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
        }
        else{
            Toast.makeText(this, "Enter your email and password", Toast.LENGTH_SHORT).show()
        }
    }

    fun logIn(){
        val intent = Intent(this, SnapsActivity::class.java)
        startActivity(intent)
    }
}