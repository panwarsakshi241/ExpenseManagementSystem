package com.aapnainfotech.expensemanagementsystem.fragments.expense

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.fragments.income.IncomeFragment
import com.aapnainfotech.expensemanagementsystem.model.Expense
import com.aapnainfotech.expensemanagementsystem.model.Income
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.Month
import java.time.MonthDay
import java.time.Year
import java.util.*


class ExpenseFragment : Fragment() {

    lateinit var expenseDate: TextView
    lateinit var ref: DatabaseReference
    lateinit var saveExpense: Button
    lateinit var addExpense: EditText
    lateinit var expenseCategorySpinner: Spinner
    lateinit var expenseResourceSpinner: Spinner
    lateinit var expenseDetails: EditText

    var selectedCategory: String = ""
    var selectedresource: String = ""
    var selectedDate: String = ""
    var timeStamp = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_expense, container, false)

        expenseDate = view.findViewById(R.id.addExpenseDateTV)
        saveExpense = view.findViewById(R.id.saveExpense)
        addExpense = view.findViewById(R.id.addExpenseET)
        expenseCategorySpinner = view.findViewById(R.id.addExpenseCategorySpinner)
        expenseResourceSpinner = view.findViewById(R.id.addExpenseResourceSpinner)
        expenseDetails = view.findViewById(R.id.comment)


        //timeStamp
        val date = Date()
        timeStamp = IncomeFragment.dateTimeFormat.format(date)

        //importing the array from the arrays.xml
        val categoryArray = resources.getStringArray(R.array.category)
        val resourceArray = resources.getStringArray(R.array.Expense)

        val user = MainActivity.currentUser?.replace(".", "")

        //initiating the Firebase Database
        ref = FirebaseDatabase.getInstance().getReference("Users/"+user!!)

        Toast.makeText(activity, MainActivity.currentUser, Toast.LENGTH_LONG).show()

        //setting onClick listener to date EditText
        setDate()


        //saving the expense data to the database
        saveExpense.setOnClickListener {
            showDialogueBox()
        }

        //retrieving data from the Category Spinner
        expenseCategorySpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCategory = categoryArray.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        expenseResourceSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedresource = resourceArray.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        return view
    }

    //save expenses to database
    private fun saveExpense() {
        val expense = addExpense.text.toString().trim()
        val comment = expenseDetails.text.toString()
        selectedDate = expenseDate.text.toString().trim()

        if (comment.isEmpty()
            || comment.isEmpty()
            || selectedDate.equals(getString(R.string.choose_date_TV))
            || selectedCategory.equals(getString(R.string.select))
            || selectedresource.equals(getString(R.string.select))
        ) {

            if (expense.isEmpty()) {
                addExpense.error = "Please enter Expense !"
                return
            }
            if (comment.isEmpty()) {
                expenseDetails.error = "Please fill the details "
                return
            }
            if (selectedDate.equals(getString(R.string.choose_date_TV))) {
                expenseDate.error = "Please choose the date"
                return
            }
            if (selectedCategory.equals(getString(R.string.select))) {
                (expenseCategorySpinner.getSelectedView() as TextView).error =
                    "Please Choose some category"
                return
            }
            if (selectedresource.equals(getString(R.string.select))) {
                (expenseResourceSpinner.getSelectedView() as TextView).error =
                    "Please Choose some resource"
                return
            }

        }


        val userId: String =
            ref.push().key.toString()//push will generate unique key for every users

        val index = selectedDate.lastIndexOf('/')
        val date = selectedDate.substring(0,index)
        val path = "Expense/$date/$selectedCategory/$userId"

        val user =
            Expense(
                userId,
                expense,
                selectedDate,
                selectedCategory,
                selectedresource,
                comment,
                timeStamp
            )

        ref.child(path).setValue(user).addOnCompleteListener {
            Toast.makeText(activity, "Expense details saved successfully", Toast.LENGTH_SHORT)
                .show()
        }

        findNavController().navigate(R.id.action_expenseFragment_to_homeFragment)


    }

    // set date
    private fun setDate() {

        expenseDate.setOnClickListener {


            val datepickerDialogue = DatePickerDialog(
                requireContext(), DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDate ->


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

                    expenseDate.setText("$mYear/$month/$mDate")
                }, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )

            datepickerDialogue.show()

        }

    }

    private fun showDialogueBox() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Are You Sure ??")
        builder.setMessage(
            "The Expense details are all correct?" +
                    " You wouldn't be able to make changes after you press okay ."
        )
        builder.setPositiveButton("Yes", { _: DialogInterface, _: Int ->
            saveExpense()
        })
        builder.setNegativeButton("No", { _: DialogInterface, _: Int ->
        })
        builder.show()
    }

}