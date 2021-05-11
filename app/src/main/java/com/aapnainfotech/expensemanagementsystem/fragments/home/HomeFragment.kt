package com.aapnainfotech.expensemanagementsystem.fragments.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    lateinit var addIncome: Button
    lateinit var addExpense: Button
    lateinit var addTrasfer: Button
    lateinit var ViewReport: Button
    lateinit var spinner: Spinner
    lateinit var incomeTV: TextView
    lateinit var expenseTV: TextView
    lateinit var currentBalanceTV: TextView

    companion object {
        var selectedSpinnerItem: String = ""
        var user: String? = ""
        var CalculatedIncome: Double = 0.0
        var CalculatedExpense: Double = 0.0
        var InitialAccountIncome: Double = 0.0
        var totalIncome: Double = 0.0
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = View.inflate(context, R.layout.fragment_home, null)

        addIncome = view.findViewById(R.id.addincome)
        addExpense = view.findViewById(R.id.addexpense)
        addTrasfer = view.findViewById(R.id.addtransfer)
        ViewReport = view.findViewById(R.id.view)
        incomeTV = view.findViewById(R.id.income_shown)
        expenseTV = view.findViewById(R.id.expense_shown)
        currentBalanceTV = view.findViewById(R.id.current_balance_shown)

        spinner = view.findViewById(R.id.spinner)

        user = MainActivity.currentUser?.replace(".", "")

        val spinnerArray = resources.getStringArray(R.array.expenseResources)

        setHasOptionsMenu(true)

        spinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedSpinnerItem = spinnerArray.get(p2)

                if (selectedSpinnerItem.equals(getString(R.string.select))) {

                    Toast.makeText(
                        activity,
                        "Please select category !!",
                        Toast.LENGTH_LONG
                    )
                        .show()

                } else if (selectedSpinnerItem.equals(getString(R.string.allAccounts))) {
                    Toast.makeText(
                        activity,
                        "yet to be implemented",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    calculateInitialAccountIncome()
                    calculateExpense()
//                    calculateCurrentBalance()
                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }


        }

        addIncome.setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_incomeFragment)
        }
        addExpense.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_expenseFragment)
        }

        addTrasfer.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_transferFragment)
        }

        ViewReport.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_viewFragment)
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.setting) {
            findNavController().navigate(R.id.settingFragment)
        }
        return super.onOptionsItemSelected(item)
    }


    // retrieving data from the  Income
    fun calculateIncome() {

        CalculatedIncome = 0.0

        FirebaseDatabase.getInstance().getReference(user!! + "/Income/" + selectedSpinnerItem)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (i in snapshot.children) {

                        val key = i.key.toString()
                        val income = snapshot.child("$key/income").value.toString()
                        CalculatedIncome += income.toInt()
                    }

                    Toast.makeText(activity, "$CalculatedIncome", Toast.LENGTH_LONG).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }


    //retrieving data from the Account database
    fun calculateInitialAccountIncome() {
        calculateIncome()
        totalIncome = 0.0
        FirebaseDatabase.getInstance().getReference(user!! + "/Account/" + selectedSpinnerItem)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val income = snapshot.child("amount").value.toString()
                    InitialAccountIncome = income.toDouble()

                    totalIncome = InitialAccountIncome + CalculatedIncome
                    incomeTV.text = totalIncome.toString()
                    Toast.makeText(activity, "$totalIncome", Toast.LENGTH_LONG).show()
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }


    fun calculateExpense() {

        CalculatedExpense = 0.0

        FirebaseDatabase.getInstance().getReference(user!! + "/Expense/" + selectedSpinnerItem)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (i in snapshot.children) {

                        val key = i.key.toString()
                        val expense = snapshot.child("$key/expense").value.toString()
                        CalculatedExpense += expense.toInt()
                    }
                    expenseTV.text = CalculatedExpense.toString()

                    val currentBalance = totalIncome - CalculatedExpense

                    currentBalanceTV.text = currentBalance.toString()

                    Toast.makeText(
                        activity,
                        CalculatedExpense.toString() + " and " + totalIncome,
                        Toast.LENGTH_LONG
                    )
                        .show()

                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }


    private fun calculateCurrentBalance() {

        calculateInitialAccountIncome()
        calculateIncome()

        Toast.makeText(
            activity,
            "${totalIncome - CalculatedExpense} ",
            Toast.LENGTH_LONG
        )
            .show()
//        currentBalanceTV.text = currentBalance.toString()

    }


    //calculate all account income details
    private fun CalculateAllAcountIncome() {


    }

}