@file:Suppress("RedundantSamConstructor")

package com.aapnainfotech.expensemanagementsystem.register

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {


    private lateinit var enterMail: EditText
    private lateinit var enterPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signIn: TextView
    private lateinit var buttonRegister: TextView
    private lateinit var auth : FirebaseAuth

    private var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ExpenseManagementSystem)
        supportActionBar?.hide()
        setContentView(R.layout.activity_registration)

        enterMail = findViewById(R.id.et_registration_mail)
        enterPassword = findViewById(R.id.et_registration_password)
        confirmPassword = findViewById(R.id.et_regisration_confirmPassword)

        signIn = findViewById(R.id.tv_signIn)
        buttonRegister = findViewById(R.id.btn_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Profile")


        signIn.setOnClickListener {
            onBackPressed()
        }

        register()

    }

    private fun register() {

        buttonRegister.setOnClickListener {
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
                confirmPassword.text.toString() != enterPassword.text.toString() -> {

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
                            OnCompleteListener { task ->

                                //if registration is successfully done
                                if (task.isSuccessful) {

                                    val currentUser = auth.currentUser
                                    val currentUserDB = databaseReference?.child(currentUser?.uid!!)
                                    currentUserDB?.child("username")?.setValue(enterMail.text.toString())

                                    Toast.makeText(this, "You are Registered Successfully !", Toast.LENGTH_LONG).show()

                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                    //if registration is not successful show error message
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