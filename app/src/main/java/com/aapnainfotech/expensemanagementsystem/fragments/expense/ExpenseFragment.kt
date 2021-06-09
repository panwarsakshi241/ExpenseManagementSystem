package com.aapnainfotech.expensemanagementsystem.fragments.expense

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.NetworkConnection
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.fragments.income.IncomeFragment
import com.aapnainfotech.expensemanagementsystem.model.Expense
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class ExpenseFragment : Fragment() {

    private lateinit var expenseDate: TextView
    private lateinit var ref: DatabaseReference
    private lateinit var saveExpense: Button
    private lateinit var addExpense: EditText
    private lateinit var expenseCategorySpinner: Spinner
    private lateinit var expenseResourceSpinner: Spinner
    private lateinit var expenseDetails: EditText

    var selectedCategory: String = ""
    var selectedResource: String = ""
    private var selectedDate: String = ""
    private var timeStamp = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_expense, container, false)

        expenseDate = view.findViewById(R.id.tv_pick_date)
        saveExpense = view.findViewById(R.id.btn_save_expense)
        addExpense = view.findViewById(R.id.et_add_expense)
        expenseCategorySpinner = view.findViewById(R.id.spinner_expense_category)
        expenseResourceSpinner = view.findViewById(R.id.spinner_expense_resource)
        expenseDetails = view.findViewById(R.id.et_comment)


        //timeStamp
        val date = Date()
        timeStamp = IncomeFragment.dateTimeFormat.format(date)

        //importing the array from the arrays.xml
        val categoryArray = resources.getStringArray(R.array.category)
        val resourceArray = resources.getStringArray(R.array.Expense)

        val user = MainActivity.currentUser?.replace(".", "")

        //initiating the Firebase Database
        ref = FirebaseDatabase.getInstance().getReference("Users/" + user!!)

        Toast.makeText(activity, MainActivity.currentUser, Toast.LENGTH_LONG).show()

        //setting onClick listener to date EditText
        setDate()

        /**
         * validate network connection
         */

        validateNetworkConnection()

        //saving the expense data to the database
        saveExpense.setOnClickListener {
            showDialogueBox()
        }

        //retrieving data from the Category Spinner
        expenseCategorySpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCategory = categoryArray[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        expenseResourceSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedResource = resourceArray[p2]
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
            || selectedDate == getString(R.string.choose_date_TV)
            || selectedCategory == getString(R.string.select)
            || selectedResource == getString(R.string.select)
        ) {

            if (expense.isEmpty()) {
                addExpense.error = "Please enter Expense !"
                return
            }
            if (comment.isEmpty()) {
                expenseDetails.error = "Please fill the details "
                return
            }
            if (selectedDate == getString(R.string.choose_date_TV)) {
                expenseDate.error = "Please choose the date"
                return
            }
            if (selectedCategory == getString(R.string.select)) {
                (expenseCategorySpinner.selectedView as TextView).error =
                    "Please Choose some category"
                return
            }
            if (selectedResource == getString(R.string.select)) {
                (expenseResourceSpinner.selectedView as TextView).error =
                    "Please Choose some resource"
                return
            }

        }


        val userId: String =
            ref.push().key.toString()//push will generate unique key for every users

        val index = selectedDate.lastIndexOf('/')
        val date = selectedDate.substring(0, index)
        val path = "Expense/$date/$selectedCategory/$userId"

        val user =
            Expense(
                userId,
                expense,
                selectedDate,
                selectedCategory,
                selectedResource,
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


            val datePickerDialogue = DatePickerDialog(
                requireContext(), { _, mYear, mMonth, mDate ->


                    var month = ""

                    when (mMonth) {
                        0 -> {
                            month = resources.getString(R.string.jan)
                        }
                        1 -> {
                            month = resources.getString(R.string.feb)
                        }
                        2 -> {
                            month = resources.getString(R.string.march)
                        }
                        3 -> {
                            month = resources.getString(R.string.april)
                        }
                        4 -> {
                            month = resources.getString(R.string.may)
                        }
                        5 -> {
                            month = resources.getString(R.string.june)
                        }
                        6 -> {
                            month = resources.getString(R.string.july)
                        }
                        7 -> {
                            month = resources.getString(R.string.aug)
                        }
                        8 -> {
                            month = resources.getString(R.string.sep)
                        }
                        9 -> {
                            month = resources.getString(R.string.oct)
                        }
                        10 -> {
                            month = resources.getString(R.string.nov)
                        }
                        11 -> {
                            month = resources.getString(R.string.dec)
                        }
                        else -> {
                            Toast.makeText(
                                activity,
                                "please choose a category !",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
                    val date = "$mYear/$month/$mDate"
                    expenseDate.text = date
                }, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialogue.show()

        }

    }

    private fun showDialogueBox() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Are You Sure ??")
        builder.setMessage(
            "The Expense details are all correct?" +
                    " You wouldn't be able to make changes after you press okay ."
        )
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            saveExpense()
            closeKeyboard(expenseDetails)
        }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int ->
        }
        builder.show()
    }

    //function to close keyboard
    private fun closeKeyboard(view: View) {

        val inputMethodManager: InputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

    }

    /**
     * validate the internet connection
     */
    private fun validateNetworkConnection() {

        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {

                saveExpense.isEnabled = true

            } else {

                saveExpense.isEnabled = false

                Toast.makeText(
                    activity,
                    "No Internet !! please try again.",
                    Toast.LENGTH_LONG
                )
                    .show()
            }

        })

    }


}