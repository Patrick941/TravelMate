package com.example.mapstemplate

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginScreen : AppCompatActivity() {

    private val thisName = "Login Screen"

    private lateinit var sharedFile:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor

    private lateinit var signUpButton : Button
    private lateinit var password : TextView
    private lateinit var username : TextView
    private lateinit var logInButton : Button
    private lateinit var nickname : TextView

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MyTag", "Program Created")
        setContentView(R.layout.activity_login_screen)

        mAuth = FirebaseAuth.getInstance()

        signUpButton = findViewById(R.id.SignUpButton)
        logInButton = findViewById(R.id.LogInButton)
        password = findViewById(R.id.PasswordText)
        username = findViewById(R.id.MessageText)
        nickname = findViewById(R.id.NickNameText)

        sharedFile = getSharedPreferences("my_sf", MODE_PRIVATE)
        editor = sharedFile.edit()


        signUpButton.setOnClickListener{
            Log.i("MyTag", "Storing password as ${password.text} and storing username as ${username.text}")
            editor.apply{
                putString("storedPassword", password.text.toString())
                putString("storedUsername", username.text.toString())
                putString("storedNickname", nickname.text.toString())
                editor.commit()
            }
            signUp(username.text.toString(), password.text.toString(), nickname.text.toString())
        }

        logInButton.setOnClickListener{
            Log.i("MyTag", "Storing password as ${password.text} and storing username as ${username.text}")
            editor.apply{
                putString("storedPassword", password.text.toString())
                putString("storedUsername", username.text.toString())
                putString("storedNickname", nickname.text.toString())
                editor.commit()
            }
            logIn(username.text.toString(), password.text.toString())
        }

    }

    private fun logIn(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("MyTag", "Log-In:success")
                Toast.makeText(baseContext, "Log-In success",
                    Toast.LENGTH_SHORT).show()
                val user = mAuth.currentUser
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                // If sign in fails, display a message to the user.
                Log.w("MyTag", "Log-In With Email:failure", task.exception)
                Toast.makeText(baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUp(email: String, password : String, nick : String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                Log.i("MyTag", "email inside sign up is $email, password is $password")
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("MyTag", "createUserWithEmail:success")
                    Toast.makeText(baseContext, "New Account Created.",
                        Toast.LENGTH_SHORT).show()
                    val user = mAuth.currentUser
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("user", email)
                    intent.putExtra("password", password)
                    addUserToDatabase(email, mAuth.currentUser?.uid!!, nick)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MyTag", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(email: String, uid: String, user : String){
        mDbRef = FirebaseDatabase.getInstance().reference
        Log.i("MyTag", "Database reference is ${mDbRef.database.reference}")

        mDbRef.child("user").child(uid).setValue(User(email, uid, user))
    }

    override fun onPause(){
        super.onPause()
        Log.i("MyTag", "pausing $thisName")
    }

    override fun onResume(){
        super.onResume()
        Log.i("MyTag", "resuming $thisName")
        password.text = sharedFile.getString("storedPassword", "").toString()
        username.text = sharedFile.getString("storedUsername", "").toString()
        nickname.text = sharedFile.getString("storedNickname", "").toString()
        Log.i("MyTag", "Restored username is ${username.text}, restored password is ${password.text}")
    }

    override fun onStart(){
        super.onStart()
        Log.i("MyTag", "starting $thisName")
    }

    override fun onStop(){
        super.onStop()
        Log.i("MyTag", "stopping $thisName")
    }

    override fun onRestart(){
        super.onRestart()
        Log.i("MyTag", "restarting $thisName")
    }

    override fun onDestroy(){
        super.onDestroy()
        Log.i("MyTag", "destroying $thisName")
    }
}