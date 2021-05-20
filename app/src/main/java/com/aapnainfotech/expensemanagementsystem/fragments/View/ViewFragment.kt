package com.aapnainfotech.expensemanagementsystem.fragments.View

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.adapter.MyAdapter
import com.aapnainfotech.expensemanagementsystem.fragments.home.HomeFragment.Companion.selectedSpinnerItem
import com.aapnainfotech.expensemanagementsystem.model.Expense
import com.google.firebase.database.*
import java.util.*


class ViewFragment : Fragment() {

    lateinit var list: ListView

    //    lateinit var ref: DatabaseReference
    lateinit var expenseList: MutableList<Expense>
    lateinit var selectDate: TextView
    lateinit var totalExpenseCalculated: TextView

    lateinit var searchByDate: Button
    lateinit var adapter: MyAdapter

    lateinit var selectFilterModeSpinner: Spinner
    var selectedFilterOption: String = ""
    var path = ""
    var dateSelected = ""
    var user: String? = ""
    var spinnerValue = ""

    var index: Int = 0
    var date: String = ""
    var expense = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_view, container, false)
        list = view.findViewById(R.id.viewList)
        selectFilterModeSpinner = view.findViewById(R.id.selectFilterOption)
        searchByDate = view.findViewById(R.id.viewbyDateButton)
        selectDate = view.findViewById(R.id.DateEntered)

        totalExpenseCalculated = view.findViewById(R.id.totalExpenseCalculated)

        expenseList = mutableListOf()

        user = MainActivity.currentUser?.replace(".", "")
        spinnerValue = selectedSpinnerItem

        //retrieving data from Select number of days spinner
        filterBySpinnerValue()

        adapter = MyAdapter(
            requireContext(),
            R.layout.row, expenseList
        )
        list.adapter = adapter

        //set on click listener on selectDate TextView
        setDate()

        //view reports of selected date
        searchByDate.setOnClickListener {

            genrateReport()

        }

        return view
    }


    fun genrateReport() {

        list.visibility = View.VISIBLE
        dateSelected = selectDate.text.toString()

        index = dateSelected.lastIndexOf('/')
        date = dateSelected.substring(0, index)

        if(selectedFilterOption.equals(getString(R.string.select))){
            (selectFilterModeSpinner.getSelectedView() as TextView).error =
                "Please Choose some category"
            return
        }

        if (selectedFilterOption.equals(getString(R.string.date))) {

            //view reports of selected date

            val query: Query =
                FirebaseDatabase.getInstance().getReference("Users/$user/Expense/$date/$spinnerValue")
                    .orderByChild("date")
                    .equalTo(dateSelected)
            query.addListenerForSingleValueEvent(valueEventListener)

        } else {

            val query: Query =
                FirebaseDatabase.getInstance().getReference("Users/$user/Expense/$date/$spinnerValue")

            query.addListenerForSingleValueEvent(valueEventListener)


        }

    }


    val valueEventListener =
        FirebaseDatabase.getInstance().getReference("Users/$user/Expense/$date/$spinnerValue")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        expenseList.clear()

                        for (i in snapshot.children) {

                            val exp = i.getValue(Expense::class.java)
                            expenseList.add(exp!!)

                        }

                        totalExpenseCalculated.text = expense
                        adapter.notifyDataSetChanged()
                    }


                }

                /**
                 * DataSnapshot contains all the values of this database node (ref)
                 *
                 */

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    // set date
    private fun setDate() {

        selectDate.setOnClickListener {

            val datepickerDialogue = DatePickerDialog(
                requireContext(), DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDate ->

                    var month = ""

                    if (mMonth == 0) {
                        month = "January"
                    }
                    if (mMonth == 1) {
                        month = "February"
                    }
                    if (mMonth == 2) {
                        month = "March"
                    }
                    if (mMonth == 3) {
                        month = "April"
                    }
                    if (mMonth == 4) {
                        month = "May"
                    }
                    if (mMonth == 6) {
                        month = "June"
                    }
                    if (mMonth == 7) {
                        month = "July"
                    }
                    if (mMonth == 8) {
                        month = "August"
                    }
                    if (mMonth == 9) {
                        month = "September"
                    }
                    if (mMonth == 10) {
                        month = "October"
                    }
                    if (mMonth == 11) {
                        month = "November"
                    }
                    if (mMonth == 12) {
                        month = "December"
                    }

                    selectDate.setText("$mYear/$month/$mDate")
                }, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )

            datepickerDialogue.show()

        }

    }

    fun filterBySpinnerValue() {

        selectFilterModeSpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val filterByArray = resources.getStringArray(R.array.selectfilterMode)
                selectedFilterOption = filterByArray.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }

}