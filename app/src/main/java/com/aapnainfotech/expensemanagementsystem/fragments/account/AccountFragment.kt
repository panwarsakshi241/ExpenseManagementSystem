package com.aapnainfotech.expensemanagementsystem.fragments.account

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.fragments.income.IncomeFragment
import com.aapnainfotech.expensemanagementsystem.model.Account
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class AccountFragment : Fragment() {

    private lateinit var initialAmt: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var saveBtn: Button

    var selectedCategory = ""
    private var timeStamp = ""

    private lateinit var ref: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        initialAmt = view.findViewById(R.id.et_initial_amount)
        categorySpinner = view.findViewById(R.id.spinner_category)
        saveBtn = view.findViewById(R.id.InitialAmountSave)

        //timeStamp

        val date = Date()
        timeStamp = IncomeFragment.dateTimeFormat.format(date)

        val user = MainActivity.currentUser?.replace(".", "")
        ref = FirebaseDatabase.getInstance().getReference("Users/" + user!!)

        //retrieve data from the Select category Spinner

        categorySpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val categoryArray = resources.getStringArray(R.array.category)
                selectedCategory = categoryArray[p2]

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        saveBtn.setOnClickListener {

            showDialogueBox()

        }

        return view
    }

    private fun saveDetails() {

        val initialAmount = initialAmt.text.toString().trim()

        if (initialAmount.isEmpty() || categorySpinner.isEmpty() || selectedCategory == getString(R.string.select)) {
            if (initialAmount.isEmpty()) {
                initialAmt.error = getString(R.string.text_view_error)
                return
            }
            if (categorySpinner.isEmpty() || selectedCategory == getString(R.string.select)) {
                (categorySpinner.selectedView as TextView).error =
                    getString(R.string.category_spinner_error)
                return
            }

        }

        val userId: String =
            ref.push().key.toString()//push will generate unique key for every users
        val path = "Account/$selectedCategory"

        val user = Account(userId, initialAmount.toDouble(), selectedCategory, timeStamp)

        ref.child(path).setValue(user).addOnCompleteListener {
            Toast.makeText(
                activity,
                getString(R.string.account_details_saved_successfully),
                Toast.LENGTH_SHORT
            )
                .show()

            findNavController().navigate(R.id.homeFragment)
        }

    }

    //dialogue Box
    private fun showDialogueBox() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.dialogbox_title))
        builder.setMessage(
            getString(R.string.dialogbox_message)
        )
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            saveDetails()
            closeKeyboard(initialAmt)
        }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int ->
        }
        builder.show()
    }

    //funtion to close keyboard
    private fun closeKeyboard(view: View){

        val inputMethodManager: InputMethodManager =activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)

    }

}