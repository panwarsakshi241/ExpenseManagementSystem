package com.aapnainfotech.expensemanagementsystem.fragments.income

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.model.Income
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


class IncomeFragment : Fragment() {

    private lateinit var addDate: TextView
    private lateinit var ref: DatabaseReference
    private lateinit var saveIncome: Button
    private lateinit var addIncome: EditText
    private lateinit var addIncomeCategorySpinner: Spinner
    private lateinit var addIncomeResourceSpinner: Spinner

    var selectedCategory: String = ""
    var selectedResource: String = ""
    private var selectedDate: String = ""
    private var timeStamp = ""
    var user : String? = ""

    companion object {
        val dateTimeFormat = SimpleDateFormat("yyyy/MM/dd hh:mm:ss",Locale.ENGLISH)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_income, container, false)

        user = MainActivity.currentUser?.replace(".", "")
        ref = FirebaseDatabase.getInstance().getReference("Users/"+user!!)

        addDate = view.findViewById(R.id.addDateTV)
        saveIncome = view.findViewById(R.id.incomeSave)
        addIncome = view.findViewById(R.id.addIncomeET)
        addIncomeCategorySpinner = view.findViewById(R.id.spinner_expense_category)
        addIncomeResourceSpinner = view.findViewById(R.id.addResourceSpinner)

        //timeStamp
        val date = Date()
        timeStamp = dateTimeFormat.format(date)

        saveIncome.setOnClickListener {
            showDialogueBox()
        }

        addDate.setOnClickListener {
            setDate()
        }

        // retrieving data from the Income category spinner
        addIncomeCategorySpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val categoryArray = resources.getStringArray(R.array.category)
                selectedCategory = categoryArray[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        //retrieve data from the Income Resource Spinner

        addIncomeResourceSpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val resourceArray = resources.getStringArray(R.array.resource)
                selectedResource = resourceArray[p2]

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        return view
    }

    private fun saveIncomeDetails() {
        val income = addIncome.text.toString().trim()
        selectedDate = addDate.text.toString().trim()

        if (income.isEmpty()
            || selectedCategory == getString(R.string.select)
            || selectedResource == getString(R.string.select)
            || selectedDate == getString(R.string.choose_date_TV)
        ) {

            if (income.isEmpty()) {
                addIncome.error = "Please enter Income !"
                return
            }

            if (selectedCategory == getString(R.string.select)) {
                (addIncomeCategorySpinner.selectedView as TextView).error =
                    "Please Choose some category"
                return
            }

            if (selectedResource == getString(R.string.select)) {
                (addIncomeResourceSpinner.selectedView as TextView).error =
                    "Please Choose some resource"
                return
            }
            if (selectedDate == getString(R.string.choose_date_TV)) {
                addDate.error = "Please select date"
                return
            }

        }


        val userId: String =
            ref.push().key.toString()//push will generate unique key for every users

        val index = selectedDate.lastIndexOf('/')
        val date = selectedDate.substring(0,index)
        val path = "Income/$date/$selectedCategory/$userId"

        val user =
            Income(userId, income, selectedDate, selectedCategory, selectedResource, timeStamp)

        ref.child(path).setValue(user).addOnCompleteListener {
            Toast.makeText(activity, "Income details saved successfully", Toast.LENGTH_SHORT).show()
        }

        findNavController().navigate(R.id.action_incomeFragment_to_homeFragment)

    }

    private fun setDate() {

        val datePickerDialogue = DatePickerDialog(
            requireContext(), { _, mYear, mMonth, mDate ->

                var month =""

                if(mMonth == 0){
                   month  = "January"
                }
                if(mMonth == 1){
                    month  = "February"
                }
                if(mMonth == 2){
                    month  = "March"
                }
                if(mMonth == 3){
                    month  = "April"
                }
                if(mMonth == 4){
                    month  = "May"
                }
                if(mMonth == 6){
                    month  = "June"
                }
                if(mMonth == 7){
                    month  = "July"
                }
                if(mMonth == 8){
                    month  = "August"
                }
                if(mMonth == 9){
                    month  = "September"
                }
                if(mMonth == 10){
                    month  = "October"
                }
                if(mMonth == 11){
                    month  = "November"
                }
                if(mMonth == 12){
                    month  = "December"
                }

                val date = "$mYear/$month/$mDate"
                addDate.text = date
            }, Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialogue.show()

    }


    //dialogue Box
    private fun showDialogueBox() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Are You Sure ??")
        builder.setMessage(
            "The Income details are all correct?" +
                    " You wouldn't be able to make changes after you press okay ."
        )
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            saveIncomeDetails()
        }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int ->
        }
        builder.show()
    }


}