@file:Suppress("PrivatePropertyName")

package com.aapnainfotech.expensemanagementsystem.fragments.View

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aapnainfotech.expensemanagementsystem.MainActivity
import com.aapnainfotech.expensemanagementsystem.NetworkConnection
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.adapter.recyclerviewAdapter.MyRecyclerViewAdapter
import com.aapnainfotech.expensemanagementsystem.fragments.home.HomeFragment.Companion.selectedSpinnerItem
import com.aapnainfotech.expensemanagementsystem.model.Expense
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_view.*
import kotlinx.android.synthetic.main.recyclerview_item.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class ViewFragment : Fragment(), MyRecyclerViewAdapter.OnItemClickListener {

    //    private late init var list: ListView
    private lateinit var recyclerView: RecyclerView

    //    late init var ref: DatabaseReference
    lateinit var expenseList: MutableList<Expense>
    lateinit var dateEntered: TextView
    lateinit var selectDateTV: TextView
//    late init var totalExpenseCalculated: TextView

    private lateinit var searchByDate: Button
    private lateinit var progressBar: ProgressBar
    lateinit var adapter: MyRecyclerViewAdapter

    private lateinit var selectFilterModeSpinner: Spinner
    var selectedFilterOption: String = ""
    var path = ""
    private var dateSelected = ""
    var user: String? = ""
    private var spinnerValue = ""

    private var index: Int = 0
    var date: String = ""
    var expense = ""

    private val STORAGE_PERMISSION_CODE: Int = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_view, container, false)
//        list = view.findViewById(R.id.viewList)
        recyclerView = view.findViewById(R.id.recycler_view)

        selectFilterModeSpinner = view.findViewById(R.id.selectFilterOption)
        searchByDate = view.findViewById(R.id.viewbyDateButton)
        dateEntered = view.findViewById(R.id.DateEntered)
        selectDateTV = view.findViewById(R.id.selectDate)
        progressBar = view.findViewById(R.id.progressBar)

        expenseList = mutableListOf()


        user = MainActivity.currentUser?.replace(".", "")
        spinnerValue = selectedSpinnerItem

        //retrieving data from Select number of days spinner
        filterBySpinnerValue()

        adapter = MyRecyclerViewAdapter(expenseList, this)
        recyclerView.adapter = MyRecyclerViewAdapter(expenseList, this)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        /**
         *set on click listener on selectDate TextView
         */

        setDate()

        /**
         * view reports of selected date
         */
        searchByDate.setOnClickListener {

//            progressBar.visibility = View.VISIBLE
//            generateReport()

            /**
             *Validate network connection
             */

            validateNetworkConnection()

        }

        return view
    }


    private fun generateReport() {

//        progressBar.visibility = View.GONE

        recyclerView.visibility = View.VISIBLE
        dateSelected = dateEntered.text.toString()

        index = dateSelected.lastIndexOf('/')
        date = dateSelected.substring(0, index)

//        if(dateSelected.isEmpty()){
//            dateEntered.error = "Please fill all the required field !"
//            return
//        }
//        if (spinnerValue == getString(R.string.allAccounts)) {
//
//            //all account cumulative report
//            if (selectedFilterOption == getString(R.string.select)) {
//                (selectFilterModeSpinner.selectedView as TextView).error =
//                    "Please Choose some category"
//                return
//            }
//            if (selectedFilterOption == getString(R.string.date)) {
//
//                //view reports of selected date
//                val categoryArray = resources.getStringArray(R.array.expenseResources)
//                val len = categoryArray.size
//
//                for (i in 1 until len - 1) {
//                    val spinnerValue = categoryArray[i]
//
//                    val query: Query =
//                        FirebaseDatabase.getInstance()
//                            .getReference("Users/$user/Expense/$date/$spinnerValue")
//                            .orderByChild("date")
//                            .equalTo(dateSelected)
//                    query.addListenerForSingleValueEvent(valueEventListener)
//
//
//                }
//            } else {
//
////                val categoryArray = resources.getStringArray(R.array.expenseResources)
////                val len = categoryArray.size
//
////                for (i in 1 until len - 1) {
////                    val spinnerValue = categoryArray.get(i)
//
//                val query1: Query =
//                    FirebaseDatabase.getInstance()
//                        .getReference("Users/$user/Expense/$date")
//
//                query1.addListenerForSingleValueEvent(valueEventListener)
////                }
//
//
//            }
//
//
//        } else {
        if (selectedFilterOption == getString(R.string.select)) {
            (selectFilterModeSpinner.selectedView as TextView).error =
                "Please Choose some category"
            return
        }

//        if (selectedFilterOption == getString(R.string.date)) {
//
//            //view reports of selected date
//
//            val query: Query =
//                FirebaseDatabase.getInstance()
//                    .getReference("Users/$user/Expense/$date/$spinnerValue")
//                    .orderByChild("date")
//                    .equalTo(dateSelected)
//            query.addListenerForSingleValueEvent(valueEventListener)
//
//            Toast.makeText(activity, "spinner value is date : $date", Toast.LENGTH_LONG).show()

//        }
        else {

            val query: Query =
                FirebaseDatabase.getInstance()
                    .getReference("Users/$user/Expense/$date/$spinnerValue")

            query.addListenerForSingleValueEvent(valueEventListener)

            Toast.makeText(activity, "spinner value is month or year", Toast.LENGTH_LONG).show()


        }
//        }

    }


    private val valueEventListener =
        FirebaseDatabase.getInstance().getReference("Users/$user/Expense/$date")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        expenseList.clear()

                        for (i in snapshot.children) {

                            val exp = i.getValue(Expense::class.java)
                            expenseList.add(exp!!)

                        }

//                        totalExpenseCalculated.text = expense
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

        dateEntered.setOnClickListener {

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

                    when (selectedFilterOption) {
                        getString(R.string.year) -> {

                            dateEntered.text = mYear.toString()
                            searchByDate.visibility = View.VISIBLE

                        }
                        getString(R.string.month) -> {

                            val selectedMonth = "$mYear/$month "
                            dateEntered.text = selectedMonth
                            searchByDate.visibility = View.VISIBLE

                        }
                        else -> {

                            val date = "$mYear/$month/$mDate"
                            dateEntered.text = date
                            searchByDate.visibility = View.VISIBLE

                        }
                    }
                }, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialogue.show()

        }

    }

    private fun filterBySpinnerValue() {

        selectFilterModeSpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val filterByArray = resources.getStringArray(R.array.selectfilterMode)
                selectedFilterOption = filterByArray[p2]

                val selectFilter = "Select $selectedFilterOption"
                selectDateTV.text = selectFilter

                if (selectedFilterOption == getString(R.string.date)
                    || selectedFilterOption == getString(R.string.month)
                    || selectedFilterOption == getString(R.string.year)
                ) {

                    selectDateTV.visibility = View.VISIBLE
                    dateEntered.visibility = View.VISIBLE

                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }

    //dialogue Box to ask if user want to download the reports
    private fun showDialogueBox() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Do you want download the expense details ??")
        builder.setMessage(
            "Note : Downloading the expense report wouldn't remove it from the application !! "
        )
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            // save details in the internal storage of the device
            downloadExpenseReport()
        }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int ->
        }
        builder.show()
    }

    private fun downloadExpenseReport() {

        if (checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_DENIED
        ) {

            //permission denied

            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )

        } else {

            startDownloading()

        }

    }

    private fun startDownloading() {

        val totalExpense = "Total Expense  = " + tv_expense.text
        val source = "Source = " + tv_source.text
        val category = "Category = " + tv_category.text
        val details = "Details = " + tv_details.text
        val text = "$totalExpense \n $source \n $category \n $details"

        var fos: FileOutputStream? = null

        try {
            val fileName = "expenseManagementReport.txt"
//            FileOutputStream(FILE_NAME , MODE_PRIVATE)
            fos = activity?.openFileOutput(fileName, MODE_PRIVATE)
            fos?.write(text.toByteArray())

            Toast.makeText(
                activity,
                "saved to : " + activity?.filesDir + "/" + fileName,
                Toast.LENGTH_LONG
            )
                .show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (ae: IOException) {
            ae.printStackTrace()
        } finally {
            if (fos != null) {

                try {
                    fos.close()
                } catch (a: IOException) {
                    a.printStackTrace()
                }

            }
        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    startDownloading()
                } else {
                    Toast.makeText(
                        activity,
                        "Permission denied",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
    }

    //method of onItemClickListener method of the MyRecyclerView Adapter

    override fun onItemClick(position: Int) {
        Toast.makeText(
            activity,
            "download report",
            Toast.LENGTH_LONG
        )
            .show()

        showDialogueBox()
    }

    // check the network connection

    private fun validateNetworkConnection() {

        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {

                generateReport()

            } else {
                progressBar.visibility = View.VISIBLE
                iv_internetConnection.visibility = View.VISIBLE
                tv_internetConnection.visibility = View.VISIBLE
            }

        })

    }
}