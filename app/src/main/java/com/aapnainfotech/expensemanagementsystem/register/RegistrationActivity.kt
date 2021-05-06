package com.aapnainfotech.expensemanagementsystem.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {


    lateinit var enterMail: EditText
    lateinit var enterPassword: EditText
    lateinit var confirmPassword: EditText
    lateinit var signIn: TextView
    lateinit var register_btn: TextView
    lateinit var auth : FirebaseAuth

    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ExpenseManagementSystem)
        supportActionBar?.hide()
        setContentView(R.layout.activity_registration)

        enterMail = findViewById(R.id.r_emailidET)
        enterPassword = findViewById(R.id.r_passwordET)
        confirmPassword = findViewById(R.id.r_confirmPasswordET)

        signIn = findViewById(R.id.signinHere)
        register_btn = findViewById(R.id.registerBtn)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Profile")


        signIn.setOnClickListener {
            onBackPressed()
        }

        register()

    }

    private fun register() {

        register_btn.setOnClickListener {
            when {
                TextUtils.isEmpty(enterMail.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this,
                        "Please Enter email",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                TextUtils.isEmpty(enterPassword.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this,
                        "Please Enter password",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                TextUtils.isEmpty(confirmPassword.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this,
                        "Please confirm password !",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                !confirmPassword.text.toString().equals(enterPassword.text.toString()) -> {

                    Toast.makeText(
                        this,
                        "Please confirm password !",
                        Toast.LENGTH_LONG
                    )
                        .show()

                }

                else -> {
                    val email: String = enterMail.text.toString().trim { it <= ' ' }
                    val password: String = enterPassword.text.toString().trim { it <= ' ' }

                    //create an instance and create a user with email and password

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->

                                //if registeration is successfullly done
                                if (task.isSuccessful) {

                                    val currentUser = auth.currentUser
                                    val currentUserdb = databaseReference?.child(currentUser?.uid!!)
                                    currentUserdb?.child("username")?.setValue(enterMail.text.toString())

                                    Toast.makeText(this, "You are Registered Successfully !", Toast.LENGTH_LONG).show()

                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                    //if registeration is not successful show error message
                                    Toast.makeText(
                                        this,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )
                }

            }

        }

    }
}