package com.aapnainfotech.expensemanagementsystem.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.register.RegistrationActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var login: Button
    lateinit var signUp: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ExpenseManagementSystem)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.emailidET)
        password = findViewById(R.id.passwordET)
        login = findViewById(R.id.loginBtn)
        signUp = findViewById(R.id.signupHere)

        username.addTextChangedListener(loginTextWatcher)
        password.addTextChangedListener(loginTextWatcher)


        signUp.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser !=  null){
            startActivity(Intent(this , MainActivity::class.java))
            finish()
        }
        login()

    }

    //enable the disabled button

    private val loginTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            // Toast.makeText(requireContext(),"Please Fill all the Fields First !",Toast.LENGTH_LONG).show()
            Log.d("Disable button", "before Text Changed")
        }

        override fun onTextChanged(
            charsequence: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            val nameInput = username.text.toString().trim()
            val passwordInput = password.text.toString().trim()

            login.setEnabled(!nameInput.isEmpty() && !passwordInput.isEmpty())
        }

        override fun afterTextChanged(p0: Editable?) {
            //Toast.makeText(requireContext(),"Now you can Login !",Toast.LENGTH_LONG).show()
            Log.d("enable button", "After text is changed ")
        }

    }

    private fun login() {

        login.setOnClickListener {
            when {
                TextUtils.isEmpty(username.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this,
                        "Please Enter email",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                TextUtils.isEmpty(password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this,
                        "Please Enter password",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                else -> {
                    val email: String = username.text.toString().trim { it <= ' ' }
                    val pwd: String = password.text.toString().trim { it <= ' ' }

                    //create an instance and create a user with email and password

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->

                                //if authentication is successfullly done
                                if (task.isSuccessful) {

                                    Toast.makeText(
                                        this,
                                        "You are Logged in Successfully !",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    if (login.isEnabled == true) {
                                        val intent =
                                            Intent(this, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                                        startActivity(intent)
                                    }
                                    finish()
                                } else {
                                    //if authentication is not successful show error message
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