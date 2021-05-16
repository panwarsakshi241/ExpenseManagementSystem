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
import java.text.SimpleDateFormat
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
        var presentDate = ""
        val dateTimeFormat = SimpleDateFormat("YYYY/MM/DD hh:mm:ss")
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

        spinner = view.findViewById(R.id.spinner)

        user = MainActivity.currentUser?.replace(".", "")

        val index = user?.indexOf('@')
        val username = user?.substring(0, index!!)
        accountHolder.text = "Welcome , $username !!"

        val date = Date()
        presentDate = IncomeFragment.dateTimeFormat.format(date)

        val spinnerArray = resources.getStringArray(R.array.expenseResources)


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
                .getReference(user!! + "/Income/$year/$month/$selectedSpinnerItem")
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
        }catch (e : Exception){
            e.stackTrace
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
            val p_Year = pYear.toInt() - 1
            year = p_Year.toString()
        } else if (pMonth == "02") {
            month = "January"
            year = pYear
        } else if (pMonth == "03") {
            month = "February"
            year = pYear
        } else if (pMonth == "04") {
            month = "March"
            year = pYear
        } else if (pMonth == "05") {
            month = "April"
            year = pYear
        } else if (pMonth == "06") {
            month = "May"
            year = pYear
        } else if (pMonth == "07") {
            month = "June"
            year = pYear
        } else if (pMonth == "08") {
            month = "July"
            year = pYear
        } else if (pMonth == "09") {
            month = "August"
            year = pYear
        } else if (pMonth == "10") {
            month = "September"
            year = pYear
        } else if (pMonth == "11") {
            month = "October"
            year = pYear
        } else {
            month = "November"
            year = pYear
        }
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

        previousMonth()
        CalculatedExpense = 0.0

        try {
            FirebaseDatabase.getInstance()
                .getReference(user!! + "/Expense/$year/$month/$selectedSpinnerItem")
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
        } catch (e: Exception) {
            e.stackTrace
        }

    }

    fun currentDate() {


    }


    //calculate all account income details
    private fun CalculateAllAcountIncome() {


    }

}