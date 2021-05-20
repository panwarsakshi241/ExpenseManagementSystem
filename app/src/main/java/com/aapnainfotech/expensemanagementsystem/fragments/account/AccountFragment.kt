package com.aapnainfotech.expensemanagementsystem.fragments.account

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isEmpty
import androidx.navigation.fragment.findNavController
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.fragments.income.IncomeFragment
import com.aapnainfotech.expensemanagementsystem.model.Account
import com.aapnainfotech.expensemanagementsystem.model.Income
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


class AccountFragment : Fragment() {

    lateinit var  initialAmt : EditText
    lateinit var categorySpinner : Spinner
    lateinit var saveBtn : Button

    var selectedCategory = ""
    var timeStamp = ""

    lateinit var ref : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        initialAmt = view.findViewById(R.id.addInitialAmountET)
        categorySpinner = view.findViewById(R.id.addAccountCategorySpinner)
        saveBtn = view.findViewById(R.id.InitialAmountSave)

        //timeStamp

        val date = Date()
        timeStamp = IncomeFragment.dateTimeFormat.format(date)

        val user = MainActivity.currentUser?.replace(".","")
        ref = FirebaseDatabase.getInstance().getReference("Users/"+user!!)

        //retrieve data drom the Select category Spinner

        categorySpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val CategoryArray = resources.getStringArray(R.array.category)
                selectedCategory = CategoryArray.get(p2)

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

    private fun saveDetails(){

        val initialAmount = initialAmt.text.toString().trim()

        if(initialAmount.isEmpty() || categorySpinner.isEmpty() || selectedCategory.equals(getString(R.string.select))){
            if (initialAmount.isEmpty()){
                initialAmt.error = "Please enter Amount!"
                return
            }
            if (categorySpinner.isEmpty() || selectedCategory.equals(getString(R.string.select))){
                (categorySpinner.getSelectedView() as TextView ).error = "Please Choose some category"
                return
            }

        }

        val userId: String =
            ref.push().key.toString()//push will generate unique key for every users
        val path = "Account/"+selectedCategory

        val user = Account(userId, initialAmount.toDouble(), selectedCategory , timeStamp)

        ref.child(path).setValue(user).addOnCompleteListener {
            Toast.makeText(activity,
                "Account details saved successfully",
                Toast.LENGTH_SHORT)
                .show()

            findNavController().navigate(R.id.homeFragment)
        }

    }

    //dialogue Box
    private fun showDialogueBox() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Are You Sure ??")
        builder.setMessage(
            "you haven't selected the same category ?" +
                    "your data will be overwritten if you do so ."
        )
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            saveDetails()
        }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int ->
        }
        builder.show()
    }

}