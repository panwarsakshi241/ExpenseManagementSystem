package com.aapnainfotech.expensemanagementsystem.fragments.setting

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.login.LoginActivity
import com.aapnainfotech.expensemanagementsystem.service.ReminderService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import java.util.*
import kotlin.concurrent.timerTask

class SettingFragment : Fragment() {

    lateinit var setReminder: TextView
    lateinit var changePassword: TextView

    //change password dialog views
    lateinit var currentPassword: EditText
    lateinit var newpassword: EditText
    lateinit var confirmPassword: EditText
    lateinit var deleteAccount: TextView

    //firebase authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        setReminder = view.findViewById(R.id.set_Reminder_Tv)
        changePassword = view.findViewById(R.id.changePassword)
        deleteAccount = view.findViewById(R.id.deleteAccount)

        auth = FirebaseAuth.getInstance()

        val reminder = ReminderService(requireContext())

        setReminder.setOnClickListener {
            setAlarm { reminder.setRepetitiveAlarm(it) }
        }

        changePassword.setOnClickListener {

            //open a dialog box to change password
            val builder = AlertDialog.Builder(activity)
            val dialogLayout = inflater.inflate(R.layout.change_password_dialog_box, null)

            currentPassword = dialogLayout.findViewById(R.id.currentPassword) as EditText
            newpassword = dialogLayout.findViewById(R.id.NewPassword) as EditText
            confirmPassword = dialogLayout.findViewById(R.id.confirmPassword) as EditText


            with(builder) {
                setTitle("Change Password")
                setPositiveButton("change password") { _, _ ->
                    //change password
                    changePassword()
                }
                setNegativeButton("Cancel") { _, _ ->

                }
                setView(dialogLayout)
                show()
            }


        }
        deleteAccount()

        return view
    }


    private fun setAlarm(callback: (Long) -> Unit) {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            DatePickerDialog(
                requireContext(),
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                    TimePickerDialog(
                        requireContext(),
                        0,
                        { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, minute)
                            callback(this.timeInMillis)
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    ).show()
                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun changePassword() {

        if (currentPassword.text.isNotEmpty() &&
            newpassword.text.isNotEmpty() &&
            confirmPassword.text.isNotEmpty()
        ) {
            //confirm password
            if (newpassword.text.toString().equals(confirmPassword.text.toString())) {

                val user = auth.currentUser
                if (user != null && user.email != null) {
                    val credential = EmailAuthProvider
                        .getCredential(user.email!!, currentPassword.text.toString())

                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    activity,
                                    "Re-Authentication success.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                user.updatePassword(newpassword.text.toString())
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                activity,
                                                "Password changed successfully.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            auth.signOut()
                                            startActivity(
                                                Intent(
                                                    activity,
                                                    LoginActivity::class.java
                                                )
                                            )
                                            activity?.finish()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Re-Authentication failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()
                }

            } else {
                Toast.makeText(activity, "Password Mismatching !!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity, "Please enter all the fields.", Toast.LENGTH_SHORT).show()

        }

    }

    //delete Account
    fun deleteAccount() {
        deleteAccount.setOnClickListener {
            showDialogueBox()
        }
    }

    private fun showDialogueBox() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Warning !!")
        builder.setMessage(
            "Are you sure you want to delete your account ??" +
                    " Your account will be permanently deleted ."
        )
        builder.setPositiveButton("Delete") { _: DialogInterface, _: Int ->
            deleteUserAccount()
        }
        builder.setNegativeButton("Cancel") { _ : DialogInterface, _: Int ->
        }
        builder.show()
    }

    fun deleteUserAccount() {

        val firebase = FirebaseAuth.getInstance()
        val firebaseUser = firebase.currentUser
        firebaseUser.delete().addOnCompleteListener(object : OnCompleteListener<Void> {

            override fun onComplete(task: Task<Void>) {
                if (task.isSuccessful) {
                    Toast.makeText(
                        activity,
                        "Account Deleted !",
                        Toast.LENGTH_LONG
                    )
                        .show()

                    val intent = Intent(activity , LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    activity?.finish()

                } else {
                    Toast.makeText(
                        activity,
                        task.exception?.message,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        })
    }
}