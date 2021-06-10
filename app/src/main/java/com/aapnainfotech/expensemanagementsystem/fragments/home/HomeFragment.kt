package com.aapnainfotech.expensemanagementsystem.fragments.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.NetworkConnection
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.data.User
import com.aapnainfotech.expensemanagementsystem.data.UserViewModel
import com.aapnainfotech.expensemanagementsystem.fragments.income.IncomeFragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tapadoo.alerter.Alerter
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private lateinit var addIncome: ImageButton
    private lateinit var addExpense: ImageButton
    private lateinit var addTransfer: ImageButton
    private lateinit var viewReport: ImageButton
    lateinit var spinner: Spinner
    lateinit var incomeTV: TextView
    lateinit var expenseTV: TextView
    lateinit var currentBalanceTV: TextView
    private lateinit var accountHolder: TextView
    private lateinit var profilePicture: CircleImageView
    private lateinit var yearTV: TextView
    private lateinit var monthTV: TextView
    lateinit var amountTV: TextView
    lateinit var currentMonthIncome: TextView
    lateinit var currentMonthExpense: TextView
    lateinit var balanceOfThisMonth: TextView
    private lateinit var thisMonthTV: TextView
    private lateinit var previousMonthTV: TextView
    private lateinit var pieChart: PieChart
    private lateinit var forwardArrow: ImageView
    private lateinit var backwardArrow: ImageView


    private lateinit var cardView1: CardView
    private lateinit var cardView2: CardView

    private lateinit var mUserViewModel: UserViewModel

    private val pickImage: Int = 1
    private var imageUri: Uri? = null
    private var bitmap: Bitmap? = null

    companion object {

        var selectedSpinnerItem: String = ""
        var user: String? = ""
        var CalculatedIncome: Double = 0.0
        var CalculatedExpense: Double = 0.0
        var InitialAccountIncome: Double = 0.0
        var totalIncome: Double = 0.0
        var month = ""
        var year = ""
        var cMonth = ""
        var cYear = ""
        var presentDate = ""
        var allAccountExpense = 0.0
        var budgetAmount = 0.0

        var thisMonthIncome = 0.0
        var thisMonthExpense = 0.0
        var totalIncomeOfThisMonth = 0.0
        var thisMonthInitialAccountIncome = 0.0

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = View.inflate(context, R.layout.fragment_home, null)

        addIncome = view.findViewById(R.id.btn_addIncome)
        addExpense = view.findViewById(R.id.btn_addExpense)
        addTransfer = view.findViewById(R.id.btn_addTransfer)
        viewReport = view.findViewById(R.id.view)
        incomeTV = view.findViewById(R.id.tv_income_shown)
        expenseTV = view.findViewById(R.id.tv_expense_shown)
        currentBalanceTV = view.findViewById(R.id.tv_current_balance_shown)
        accountHolder = view.findViewById(R.id.accountHolder)
        profilePicture = view.findViewById(R.id.iv_profilePicture)
        yearTV = view.findViewById(R.id.year)
        monthTV = view.findViewById(R.id.month)
        amountTV = view.findViewById(R.id.amount)
        currentMonthIncome = view.findViewById(R.id.tv_this_month_income_shown)
        currentMonthExpense = view.findViewById(R.id.tv_this_month_expense_shown)
        balanceOfThisMonth = view.findViewById(R.id.tv_this_month_current_balance_shown)
        thisMonthTV = view.findViewById(R.id.tv_thisMonth)
        previousMonthTV = view.findViewById(R.id.tv_previousMonth)
        pieChart = view.findViewById(R.id.pie_chart)
        spinner = view.findViewById(R.id.spinner)

        cardView1 = view.findViewById(R.id.cardView1)
        cardView2 = view.findViewById(R.id.cardView2)

        forwardArrow = view.findViewById(R.id.forward_arrow)
        backwardArrow = view.findViewById(R.id.backward_arrow)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        user = MainActivity.currentUser?.replace(".", "")

        val index = user?.indexOf('@')
        val username = user?.substring(0, index!!)
        accountHolder.text = username

        val date = Date()
        presentDate = IncomeFragment.dateTimeFormat.format(date)

        val spinnerArray = resources.getStringArray(R.array.expenseResources)
        setMonthlyPlanData()

        spinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedSpinnerItem = spinnerArray[p2]

                when (selectedSpinnerItem) {
                    getString(R.string.select) -> {

                        Toast.makeText(
                            activity,
                            "Please select category !!",
                            Toast.LENGTH_LONG
                        )
                            .show()

                    }
                    getString(R.string.allAccounts) -> {

                        //this month
                        cumulativeInitialAccountIncomeOfThisMonth()
                        calculateAllAccountExpense()

                        //previous month
                        cumulativeInitialIncome()
                        allAccountExpenseOfPreviousMonth()


                    }
                    else -> {

                        calculateInitialAccountIncome()

                        //this month
                        calculateThisMonthExpense()
                        //previous month
                        calculateExpense()

                        //Insert data to room database
                        insertDataToRoomDatabase()
                    }
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

        addTransfer.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_transferFragment)
        }

        viewReport.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_viewFragment)
        }


        profilePicture.setOnClickListener {
            val openGalleryIntent = Intent()
            openGalleryIntent.type = "image/*"
            openGalleryIntent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(
                Intent.createChooser(openGalleryIntent, "select picture"),
                pickImage
            )
        }

        profilePicture.setOnClickListener {

        }

        getBudgetAmount()
        viewPreviousMonthReport()
        setPieChart()

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == pickImage && resultCode == RESULT_OK) {
            imageUri = data!!.data

            try {

                val inputStream: InputStream? =
                    activity?.applicationContext?.contentResolver?.openInputStream(imageUri!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                profilePicture.setImageBitmap(bitmap)

            } catch (ae: IOException) {

                ae.printStackTrace()

            }

        }
    }

    // retrieving data from the  Income
    private fun calculateIncome() {

        previousMonth()
        CalculatedIncome = 0.0


        try {
            FirebaseDatabase.getInstance()
                .getReference("Users/" + user!! + "/Income/$year/$month/$selectedSpinnerItem")
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (i in snapshot.children) {

                            val key = i.key.toString()
                            val income = snapshot.child("$key/income").value.toString()
                            CalculatedIncome += income.toInt()

                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //previous month
    private fun previousMonth() {

        val fIndex = presentDate.indexOf('/')
        val lIndex = presentDate.lastIndexOf('/')
        val pMonth = presentDate.substring(fIndex + 1, lIndex)
        val pYear = presentDate.substring(0, fIndex)

        when (pMonth) {
            "01" -> {
                month = "December"
                cMonth = "January"
                cYear = pYear
                val previousYear = pYear.toInt() - 1
                year = previousYear.toString()
            }
            "02" -> {
                month = "January"
                cMonth = "February"
                cYear = pYear
                year = pYear
            }
            "03" -> {
                month = "February"
                cMonth = "March"
                cYear = pYear
                year = pYear
            }
            "04" -> {
                month = "March"
                cMonth = "April"
                cYear = pYear
                year = pYear
            }
            "05" -> {
                month = "April"
                cMonth = "May"
                cYear = pYear
                year = pYear
            }
            "06" -> {
                month = "May"
                cMonth = "June"
                cYear = pYear
                year = pYear
            }
            "07" -> {
                month = "June"
                cMonth = "July"
                cYear = pYear
                year = pYear
            }
            "08" -> {
                month = "July"
                cMonth = "August"
                cYear = pYear
                year = pYear
            }
            "09" -> {
                month = "August"
                cMonth = "September"
                cYear = pYear
                year = pYear
            }
            "10" -> {
                month = "September"
                cMonth = "October"
                cYear = pYear
                year = pYear
            }
            "11" -> {
                month = "October"
                cMonth = "November"
                cYear = pYear
                year = pYear
            }
            else -> {
                month = "November"
                cMonth = "December"
                cYear = pYear
                year = pYear
            }
        }
    }

    //setting monthly plan
    private fun setMonthlyPlanData() {

        previousMonth()
        yearTV.text = cYear
        monthTV.text = cMonth

        val thisMonth = "Month : $cMonth"
        thisMonthTV.text = thisMonth
        val previousMonth = "Month : $month"
        previousMonthTV.text = previousMonth

    }


    //retrieving data from the Account database
    fun calculateInitialAccountIncome() {
        calculateIncome()
        calculateThisMonthIncome()
        totalIncome = 0.0
        totalIncomeOfThisMonth = 0.0
        try {
            FirebaseDatabase.getInstance()
                .getReference("Users/" + user!! + "/Account/" + selectedSpinnerItem)
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {

                            val income = snapshot.child("amount").value.toString()
                            InitialAccountIncome = income.toDouble()

                            totalIncome = InitialAccountIncome + CalculatedIncome
                            totalIncomeOfThisMonth = InitialAccountIncome + thisMonthIncome

                            incomeTV.text = totalIncome.toString()
                            currentMonthIncome.text = totalIncomeOfThisMonth.toString()

                        } catch (e: NumberFormatException) {

                            Toast.makeText(
                                activity,
                                "Please add your account details !",
                                Toast.LENGTH_LONG
                            )
                                .show()

                        }

                    }


                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (ae: Exception) {
            ae.stackTrace
        }

    }


    fun calculateExpense() {

        previousMonth()
        CalculatedExpense = 0.0

        try {
            FirebaseDatabase.getInstance()
                .getReference("Users/" + user!! + "/Expense/$year/$month/$selectedSpinnerItem")
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

                    }


                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
            e.stackTrace
        }

    }


    //function to calculate the total expense of current month

    fun calculateAllAccountExpense() {

        previousMonth()
        allAccountExpense = 0.0

        val categoryArray = resources.getStringArray(R.array.expenseResources)
        val len = categoryArray.size

        for (i in 1 until len - 1) {

            val category = categoryArray[i]
            try {

                FirebaseDatabase.getInstance()
                    .getReference("Users/" + user!! + "/Expense/$cYear/$cMonth/$category")
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (j in snapshot.children) {

                                val key = j.key.toString()
                                val expense = snapshot.child("$key/expense").value.toString()
                                allAccountExpense += expense.toInt()

                            }

                            currentMonthExpense.text = allAccountExpense.toString()

                            val currentBalance = totalIncomeOfThisMonth - allAccountExpense

                            balanceOfThisMonth.text = currentBalance.toString()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

            } catch (ae: Exception) {

                ae.stackTrace

            }

        }

    }

    // retrieve the budget amount from the firebase real time database

    private fun getBudgetAmount() {

        previousMonth()
        budgetAmount = 0.0
        calculateAllAccountExpense()

        try {

            FirebaseDatabase.getInstance()
                .getReference("Users/$user/Budget/$cYear/$cMonth")
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        try {
                            val amount = snapshot.child("amount").value.toString()

                            budgetAmount = amount.toDouble()

                            //setting budget amount in the text view
                            val budget = "Amount \n$budgetAmount"
                            amountTV.text = budget

                            if (allAccountExpense > budgetAmount) {

                                //send Alert Notification
                                sendAlertNotification()


                            }
                        } catch (e: NumberFormatException) {

                            Toast.makeText(
                                activity,
                                "Please plan your Budget for this month !",
                                Toast.LENGTH_LONG
                            )
                                .show()

                        }


                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

        } catch (e: Exception) {

            e.stackTrace

        }

    }


    // send Alert notification if the user exceeds the budget limit

    private fun sendAlertNotification() {

        Alerter.create(activity)
            .setTitle("WARNING !!")
            .setText("Expense Reached the Limit !")
            .setIcon(R.drawable.ic_warning)
            .setBackgroundColorRes(R.color.yellow_200)
            .setDuration(6000)
            .setOnClickListener {

                Toast.makeText(
                    activity,
                    "Alert Clicked !",
                    Toast.LENGTH_SHORT
                )
                    .show()

            }.show()

    }

    // function to calculate the total income (Cumulative of all accounts )of the previous month
    private fun allAccountIncomeOfPreviousMonth() {

        previousMonth()
        CalculatedIncome = 0.0

        val categoryArray = resources.getStringArray(R.array.expenseResources)
        val len = categoryArray.size

        for (i in 1 until len - 1) {

            val category = categoryArray[i]
            try {

                FirebaseDatabase.getInstance()
                    .getReference("Users/" + user!! + "/Income/$year/$month/$category")
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (j in snapshot.children) {

                                val key = j.key.toString()
                                val income = snapshot.child("$key/income").value.toString()
                                CalculatedIncome += income.toInt()

                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

            } catch (ae: Exception) {

                ae.stackTrace

            }

        }

    }

    //calculate all category initial account income
    fun cumulativeInitialIncome() {

        allAccountIncomeOfPreviousMonth()

        val categoryArray = resources.getStringArray(R.array.expenseResources)
        val len = categoryArray.size

        totalIncome = 0.0
        var totalInitialAmount = 0.0

        for (i in 1 until len - 1) {
            val category = categoryArray[i]
            try {
                FirebaseDatabase.getInstance()
                    .getReference("Users/" + user!! + "/Account/" + category)
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {

                                val income = snapshot.child("amount").value.toString()
                                InitialAccountIncome = income.toDouble()

                                totalInitialAmount += InitialAccountIncome
                                totalIncome = totalInitialAmount + CalculatedIncome


                                incomeTV.text = totalIncome.toString()

                            } catch (e: NumberFormatException) {

                                Toast.makeText(
                                    activity,
                                    getString(R.string.account_details_toast_text),
                                    Toast.LENGTH_LONG
                                )
                                    .show()

                            }

                        }


                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            } catch (ae: Exception) {
                ae.stackTrace
            }


        }


    }

    //function to calculate the total expense (Cumulative of all accounts) of the previous month
    fun allAccountExpenseOfPreviousMonth() {

        previousMonth()
        CalculatedExpense = 0.0

        val categoryArray = resources.getStringArray(R.array.expenseResources)
        val len = categoryArray.size

        for (i in 1 until len - 1) {
            val category = categoryArray[i]

            try {

                FirebaseDatabase.getInstance()
                    .getReference("Users/" + user!! + "/Expense/$year/$month/$category")
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (j in snapshot.children) {

                                val key = j.key.toString()
                                val expense = snapshot.child("$key/expense").value.toString()
                                CalculatedExpense += expense.toInt()

                            }

                            expenseTV.text = CalculatedExpense.toString()

                            val currentBalance = totalIncome - CalculatedExpense
                            currentBalanceTV.text = currentBalance.toString()

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

            } catch (ae: Exception) {

                ae.stackTrace

            }

        }

    }

    //calculate income of this month
    private fun calculateThisMonthIncome() {
        previousMonth()

        thisMonthIncome = 0.0


        try {
            FirebaseDatabase.getInstance()
                .getReference("Users/" + user!! + "/Income/$cYear/$cMonth/$selectedSpinnerItem")
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (i in snapshot.children) {

                            val key = i.key.toString()
                            val income = snapshot.child("$key/income").value.toString()
                            thisMonthIncome += income.toInt()

                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    //Calculate this month expense
    fun calculateThisMonthExpense() {

        previousMonth()
        thisMonthExpense = 0.0

        try {
            FirebaseDatabase.getInstance()
                .getReference("Users/" + user!! + "/Expense/$cYear/$cMonth/$selectedSpinnerItem")
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (i in snapshot.children) {

                            val key = i.key.toString()
                            val expense = snapshot.child("$key/expense").value.toString()
                            thisMonthExpense += expense.toInt()
                        }
                        currentMonthExpense.text = thisMonthExpense.toString()

                        val currentBalance = totalIncomeOfThisMonth - thisMonthExpense

                        balanceOfThisMonth.text = currentBalance.toString()

                    }


                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
            e.stackTrace
        }

    }

    //calculate cumulative income
    private fun cumulativeIncomeOfThisMonth() {

        previousMonth()
        thisMonthIncome = 0.0

        val categoryArray = resources.getStringArray(R.array.expenseResources)
        val len = categoryArray.size

        for (i in 1 until len - 1) {

            val category = categoryArray[i]
            try {

                FirebaseDatabase.getInstance()
                    .getReference("Users/" + user!! + "/Income/$cYear/$cMonth/$category")
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (j in snapshot.children) {

                                val key = j.key.toString()
                                val income = snapshot.child("$key/income").value.toString()
                                thisMonthIncome += income.toInt()

                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

            } catch (ae: Exception) {

                ae.stackTrace

            }

        }

    }

    //calculate cumulative income of this month
    fun cumulativeInitialAccountIncomeOfThisMonth() {

        cumulativeIncomeOfThisMonth()

        val categoryArray = resources.getStringArray(R.array.expenseResources)
        val len = categoryArray.size

        totalIncomeOfThisMonth = 0.0
        var totalInitialAmount = 0.0

        for (i in 1 until len - 1) {
            val category = categoryArray[i]
            try {
                FirebaseDatabase.getInstance()
                    .getReference("Users/" + user!! + "/Account/" + category)
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {

                                val income = snapshot.child("amount").value.toString()
                                thisMonthInitialAccountIncome = income.toDouble()

                                totalInitialAmount += thisMonthInitialAccountIncome
                                totalIncomeOfThisMonth = totalInitialAmount + thisMonthIncome


                                currentMonthIncome.text = totalIncomeOfThisMonth.toString()

                            } catch (e: NumberFormatException) {

                                Toast.makeText(
                                    activity,
                                    "Please add your account details !",
                                    Toast.LENGTH_LONG
                                )
                                    .show()

                            }

                        }


                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            } catch (ae: Exception) {
                ae.stackTrace
            }


        }


    }

    //function to set pie chart

    private fun setPieChart() {

        //x values

        val xValues = ArrayList<String>()
        xValues.add("Clothing")
        xValues.add("Bills")
        xValues.add("Food")
        xValues.add("Drink")
        xValues.add("Travel")

        //y values

        val pieChartEntry = ArrayList<Entry>()
        pieChartEntry.add(Entry(6.5f, 0))
        pieChartEntry.add(Entry(6.5f, 1))
        pieChartEntry.add(Entry(6.5f, 2))
        pieChartEntry.add(Entry(6.5f, 3))
        pieChartEntry.add(Entry(6.5f, 4))

        //colors

        val colors = ArrayList<Int>()
        colors.add(Color.RED)
        colors.add(Color.BLUE)
        colors.add(Color.YELLOW)
        colors.add(Color.GRAY)
        colors.add(Color.GREEN)

        //fill the chart

        val pieDataSet = PieDataSet(pieChartEntry, "Consumption")

        pieDataSet.colors = colors
        pieDataSet.sliceSpace = 3f

        val data = PieData(xValues, pieDataSet)
        pieChart.data = data

        pieChart.holeRadius = 5f
        pieChart.setBackgroundColor(resources.getColor(R.color.white))

    }


    /**
     * function to adjust the visibility
     * of the this month report and previous month report.
     */

    private fun viewPreviousMonthReport() {

        forwardArrow.setOnClickListener {
            cardView1.visibility = View.GONE
            cardView2.visibility = View.VISIBLE
            backwardArrow.visibility = View.VISIBLE
            forwardArrow.visibility = View.GONE
        }

        backwardArrow.setOnClickListener {
            cardView1.visibility = View.VISIBLE
            cardView2.visibility = View.GONE
            backwardArrow.visibility = View.GONE
            forwardArrow.visibility = View.VISIBLE
        }


    }

    /**
     * saving the previous month and
     * this month details in the
     * room database .
     * So that the application can work in the offline mode as well.
     */

    private fun insertDataToRoomDatabase() {
        previousMonth()
        val spinner = selectedSpinnerItem
        val pYear = year
        val pMonth = previousMonthTV.text.toString()
        val pIncome = incomeTV.text.toString()
        val pExpense = expenseTV.text.toString()
        val pCurrentBalance = currentBalanceTV.text.toString()

        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {

                val data = User(0, pYear, pMonth, pIncome, pExpense, pCurrentBalance , spinner)
                Toast.makeText(
                    activity,
                    "Successfully added",
                    Toast.LENGTH_LONG
                )
                    .show()

                mUserViewModel.addDetails(data)

            } else {

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