package com.aapnainfotech.expensemanagementsystem.fragments.setting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.login.LoginActivity
import com.aapnainfotech.expensemanagementsystem.service.ReminderService
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class SettingFragment : Fragment() {

    private lateinit var setReminder: TextView
    private lateinit var changePassword: TextView

    //change password dialog views
    private lateinit var currentPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var deleteAccount: TextView

    //firebase authentication
    private lateinit var auth: FirebaseAuth

    @SuppressLint("InflateParams")
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

            currentPassword = dialogLayout.findViewById(R.id.et_current_password) as EditText
            newPassword = dialogLayout.findViewById(R.id.et_new_password) as EditText
            confirmPassword = dialogLayout.findViewById(R.id.et_confirm_new_password) as EditText


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
            newPassword.text.isNotEmpty() &&
            confirmPassword.text.isNotEmpty()
        ) {
            //confirm password
            if (newPassword.text.toString() == confirmPassword.text.toString()) {

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

                                user.updatePassword(newPassword.text.toString())
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
    private fun deleteAccount() {
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
        builder.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
        }
        builder.show()
    }

    private fun deleteUserAccount() {

        val firebase = FirebaseAuth.getInstance()
        val firebaseUser = firebase.currentUser
        firebaseUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    activity,
                    "Account Deleted !",
                    Toast.LENGTH_LONG
                )
                    .show()

                val intent = Intent(activity, LoginActivity::class.java)
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
    }
}