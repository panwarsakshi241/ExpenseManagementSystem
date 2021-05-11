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
import com.aapnainfotech.expensemanagementsystem.model.Income
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


class ViewFragment : Fragment() {

    lateinit var list: ListView
    lateinit var viewReports: Button

    //    lateinit var ref: DatabaseReference
    lateinit var expenseList: MutableList<Expense>
    lateinit var incomeList: MutableList<Income>
    lateinit var selectDate: TextView
    lateinit var totalExpenseCalculated: TextView

    lateinit var searchByDate: Button
    lateinit var adapter: MyAdapter

    lateinit var selectDaysCount: Spinner
    var days: String = ""
    var path = ""

    var total = 0.0
    var expense = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_view, container, false)
        list = view.findViewById(R.id.viewList)
        viewReports = view.findViewById(R.id.okayButton)
        selectDaysCount = view.findViewById(R.id.selectDaysSpinner)
        searchByDate = view.findViewById(R.id.viewbyDateButton)
        selectDate = view.findViewById(R.id.DateEntered)

        totalExpenseCalculated = view.findViewById(R.id.totalExpenseCalculated)

        val daysArray = resources.getStringArray(R.array.selectNumberOfDays)

        expenseList = mutableListOf()
        incomeList = mutableListOf()

        val user = MainActivity.currentUser?.replace(".", "")
        val spinnerValue = selectedSpinnerItem
        path = user + "/Expense/$spinnerValue"

//        ref = FirebaseDatabase.getInstance().getReference(path)


        //retrieving data from Select number of days spinner
        selectDaysCount.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                days = daysArray.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        adapter = MyAdapter(
            requireContext(),
            R.layout.row, expenseList
        )
        list.adapter = adapter


        //view report as per the selected number of days
        viewReports.setOnClickListener {

            if (days.equals(getString(R.string.select))) {

                (selectDaysCount.getSelectedView() as TextView).error =
                    "Please select number of Days"

            } else {

                list.visibility = View.VISIBLE
                val query: Query =
                    FirebaseDatabase.getInstance().getReference(path).limitToLast(days.toInt())
                query.addListenerForSingleValueEvent(valueEventListener)

            }

        }

        //set on click listener on selectDate TextView
        setDate()

        //view reports of selected date
        searchByDate.setOnClickListener {
            list.visibility = View.VISIBLE
            val dateSelected = selectDate.text.toString()
            val query: Query =
                FirebaseDatabase.getInstance().getReference(path)
                    .orderByChild("date")
                    .equalTo(dateSelected)
            query.addListenerForSingleValueEvent(valueEventListener)

            Toast.makeText(activity, "Under development", Toast.LENGTH_LONG).show()
//            findExpenseByDate()
        }


        return view
    }

    val valueEventListener = FirebaseDatabase.getInstance().getReference(path)
        .addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    expenseList.clear()

                    for (i in snapshot.children) {

                        val key = i.key.toString()
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

                    selectDate.setText("$mDate/$mMonth/$mYear")
                }, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )

            datepickerDialogue.show()

        }

    }

}