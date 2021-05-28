package com.aapnainfotech.expensemanagementsystem.fragments.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.fragments.income.IncomeFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.io.InputStream
import java.lang.NumberFormatException
import com.tapadoo.alerter.Alerter
import java.util.*


class HomeFragment : Fragment() {

    lateinit var addIncome: ImageButton
    lateinit var addExpense: ImageButton
    lateinit var addTrasfer: ImageButton
    lateinit var ViewReport: ImageButton
    lateinit var spinner: Spinner
    lateinit var incomeTV: TextView
    lateinit var expenseTV: TextView
    lateinit var currentBalanceTV: TextView
    lateinit var accountHolder: TextView
    lateinit var profilePicture: CircleImageView
    lateinit var year_TV: TextView
    lateinit var month_TV: TextView
    lateinit var amount_TV: TextView
    lateinit var currentMonthIncome: TextView
    lateinit var currentMonthExpense: TextView
    lateinit var BalanceOfThisMonth: TextView
    lateinit var thisMonthTV: TextView
    lateinit var previousMonthTV : TextView

    private val PICK_IMAGE: Int = 1
    var imageUri: Uri? = null
    var bitmap: Bitmap? = null

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
        var thisMonthBalance = 0.0
        var thisMonthInitialAccountIncome = 0.0

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
        accountHolder = view.findViewById(R.id.accountHolder)
        profilePicture = view.findViewById(R.id.profilepicture)
        year_TV = view.findViewById(R.id.year)
        month_TV = view.findViewById(R.id.month)
        amount_TV = view.findViewById(R.id.amount)
        currentMonthIncome = view.findViewById(R.id.this_month_income_shown)
        currentMonthExpense = view.findViewById(R.id.this_month_expense_shown)
        BalanceOfThisMonth = view.findViewById(R.id.this_month_current_balance_shown)
        thisMonthTV = view.findViewById(R.id.thisMonth)
        previousMonthTV = view.findViewById(R.id.previousMonth)

        spinner = view.findViewById(R.id.spinner)

        user = MainActivity.currentUser?.replace(".", "")

        val index = user?.indexOf('@')
        val username = user?.substring(0, index!!)
        accountHolder.text = "Welcome , $username !!"

        val date = Date()
        presentDate = IncomeFragment.dateTimeFormat.format(date)

        val spinnerArray = resources.getStringArray(R.array.expenseResources)
        setMonthlPlanData()

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

                    //this month
                    cumulativeInitialAccountIncomeOfThisMonth()
                    calculateAllAccountExpense()

                    //previous month
                    CumulativeInitialIncome()
                    allAccountExpenseOfPreviousMonth()


                } else {

                    calculateInitialAccountIncome()

                    //this month
                    calculateThisMonthExpense()
                    //previous month
                    calculateExpense()

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


        profilePicture.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {

                val openGalleryIntent = Intent()
                openGalleryIntent.setType("image/*")
                openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT)

                startActivityForResult(
                    Intent.createChooser(openGalleryIntent, "select picture"),
                    PICK_IMAGE
                )
            }

        })

        profilePicture.setOnClickListener(View.OnClickListener {

        })

        getBudgetAmount()

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == PICK_IMAGE && resultCode == RESULT_OK) {
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
    fun calculateIncome() {

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
    fun previousMonth() {

        val f_index = presentDate.indexOf('/')
        val l_index = presentDate.lastIndexOf('/')
        val pMonth = presentDate.substring(f_index + 1, l_index)
        val pYear = presentDate.substring(0, f_index)

        if (pMonth == "01") {
            month = "December"
            cMonth = "January"
            cYear = pYear
            val p_Year = pYear.toInt() - 1
            year = p_Year.toString()
        } else if (pMonth == "02") {
            month = "January"
            cMonth = "February"
            cYear = pYear
            year = pYear
        } else if (pMonth == "03") {
            month = "February"
            cMonth = "March"
            cYear = pYear
            year = pYear
        } else if (pMonth == "04") {
            month = "March"
            cMonth = "April"
            cYear = pYear
            year = pYear
        } else if (pMonth == "05") {
            month = "April"
            cMonth = "May"
            cYear = pYear
            year = pYear
        } else if (pMonth == "06") {
            month = "May"
            cMonth = "June"
            cYear = pYear
            year = pYear
        } else if (pMonth == "07") {
            month = "June"
            cMonth = "July"
            cYear = pYear
            year = pYear
        } else if (pMonth == "08") {
            month = "July"
            cMonth = "August"
            cYear = pYear
            year = pYear
        } else if (pMonth == "09") {
            month = "August"
            cMonth = "September"
            cYear = pYear
            year = pYear
        } else if (pMonth == "10") {
            month = "September"
            cMonth = "October"
            cYear = pYear
            year = pYear
        } else if (pMonth == "11") {
            month = "October"
            cMonth = "November"
            cYear = pYear
            year = pYear
        } else {
            month = "November"
            cMonth = "December"
            cYear = pYear
            year = pYear
        }
    }

    //setting monthly plan
    fun setMonthlPlanData() {

        previousMonth()
        year_TV.text = "Year \n$cYear"
        month_TV.text = "Month \n$cMonth"

        thisMonthTV.text = "Month : $cMonth"
        previousMonthTV.text = "Previous Month : $month"

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

            val category = categoryArray.get(i)
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

                            BalanceOfThisMonth.text = currentBalance.toString()
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

    fun getBudgetAmount() {

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
                            val Amt = snapshot.child("amount").value.toString()

                            budgetAmount = Amt.toDouble()

                            //setting budegt amount in the text view
                            amount_TV.text = "Amount \n" + budgetAmount

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
            .setOnClickListener(View.OnClickListener {

                Toast.makeText(
                    activity,
                    "Alert Clicked !",
                    Toast.LENGTH_SHORT
                )
                    .show()

            }).show()

    }

    // function to calculate the total income (Commulative of all accounts )of the previous month
    fun allAccountIncomeOfPreviousMonth() {

        previousMonth()
        CalculatedIncome = 0.0

        val categoryArray = resources.getStringArray(R.array.expenseResources)
        val len = categoryArray.size

        for (i in 1 until len - 1) {

            val category = categoryArray.get(i)
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
    fun CumulativeInitialIncome() {

        allAccountIncomeOfPreviousMonth()

        val categoryArray = resources.getStringArray(R.array.expenseResources)
        val len = categoryArray.size

        totalIncome = 0.0
        var totalInitialAmount = 0.0

        for (i in 1 until len - 1) {
            val category = categoryArray.get(i)
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

    //function to calculate the total expense (commulative of all accounts) of the previous month
    fun allAccountExpenseOfPreviousMonth() {

        previousMonth()
        CalculatedExpense = 0.0

        val categoryArray = resources.getStringArray(R.array.expenseResources)
        val len = categoryArray.size

        for (i in 1 until len - 1) {
            val category = categoryArray.get(i)

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
    fun calculateThisMonthIncome() {
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

    //caculate this month epense
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

                        BalanceOfThisMonth.text = currentBalance.toString()

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
    fun cumulativeIncomeOfThisMonth() {

        previousMonth()
        thisMonthIncome = 0.0

        val categoryArray = resources.getStringArray(R.array.expenseResources)
        val len = categoryArray.size

        for (i in 1 until len - 1) {

            val category = categoryArray.get(i)
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
            val category = categoryArray.get(i)
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

}