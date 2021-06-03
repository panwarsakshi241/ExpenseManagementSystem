package com.aapnainfotech.expensemanagementsystem.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.register.RegistrationActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var signUp: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ExpenseManagementSystem)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.et_email)
        password = findViewById(R.id.et_password)
        login = findViewById(R.id.btn_login)
        signUp = findViewById(R.id.tv_signUp)

        username.addTextChangedListener(loginTextWatcher)
        password.addTextChangedListener(loginTextWatcher)


        signUp.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
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
            Timber.d(getString(R.string.timber_text))
        }

        override fun onTextChanged(
            charsequence: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            val nameInput = username.text.toString().trim()
            val passwordInput = password.text.toString().trim()

            login.isEnabled = nameInput.isNotEmpty() && passwordInput.isNotEmpty()
        }

        override fun afterTextChanged(p0: Editable?) {
            //Toast.makeText(requireContext(),"Now you can Login !",Toast.LENGTH_LONG).show()
            Timber.d(getString(R.string.enable_button_timber_text))
        }

    }

    private fun login() {

        login.setOnClickListener {
            when {
                TextUtils.isEmpty(username.text.toString().trim()) -> {
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
                            OnCompleteListener { task ->

                                //if authentication is successfully done
                                if (task.isSuccessful) {

                                    Toast.makeText(
                                        this,
                                        getString(R.string.successful_login),
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    if (login.isEnabled) {
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