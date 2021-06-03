package com.aapnainfotech.expensemanagementsystem.fragments.transfer

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
import com.aapnainfotech.expensemanagementsystem.fragments.income.IncomeFragment
import com.aapnainfotech.expensemanagementsystem.model.Expense
import com.aapnainfotech.expensemanagementsystem.model.Income
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class TransferFragment : Fragment() {

    private lateinit var addTransferDate: TextView
    private lateinit var transferButton: Button
    private lateinit var ref: DatabaseReference
    private lateinit var transferredAmountET: EditText
    private lateinit var sourceAccountSpinner: Spinner
    private lateinit var targetAccountSpinner: Spinner

    private var timeStamp = ""
    var user: String? = ""
    var sourceAccount = ""
    var targetAccount = ""
    private var selectedDate = ""
    private var transferredAmt = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transfer, container, false)

        addTransferDate = view.findViewById(R.id.addTransferDateTV)
        transferButton = view.findViewById(R.id.transferBtn)
        sourceAccountSpinner = view.findViewById(R.id.sourceAccountspinner)
        targetAccountSpinner = view.findViewById(R.id.targetAccountSpinner)
        transferredAmountET = view.findViewById(R.id.transferedAmountET)

        user = MainActivity.currentUser?.replace(".", "")
        ref = FirebaseDatabase.getInstance().getReference("Users/" + user!!)

        sourceAccountSpinnerValue()
        targetAccountSpinnerValue()

        val date = Date()
        timeStamp = IncomeFragment.dateTimeFormat.format(date)

        addTransferDate.setOnClickListener {
            setDate()
        }

        //save transfer details
        transferButton.setOnClickListener {
            showDialogueBox()
        }

        return view
    }

    //set date
    private fun setDate() {

        val datePickerDialogue = DatePickerDialog(
            requireContext(), { _, mYear, mMonth, mDate ->

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

                val date = "$mYear/$month/$mDate"
                addTransferDate.text = date
            }, Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialogue.show()

    }

    //retrieve spinner value
    private fun sourceAccountSpinnerValue() {

        sourceAccountSpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val categoryArray = resources.getStringArray(R.array.category)
                sourceAccount = categoryArray[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }

    private fun targetAccountSpinnerValue() {

        targetAccountSpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val categoryArray = resources.getStringArray(R.array.category)
                targetAccount = categoryArray[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }

    private fun saveTransferDetails() {

        transferredAmt = transferredAmountET.text.toString().trim()
        selectedDate = addTransferDate.text.toString().trim()

        if (transferredAmt.isEmpty()) {
            transferredAmountET.error = "Please enter Income !"
            return
        }

        if (sourceAccount == getString(R.string.select)) {
            (sourceAccountSpinner.selectedView as TextView).error =
                "Please Choose some category"
            return
        }

        if (targetAccount == getString(R.string.select)) {
            (targetAccountSpinner.selectedView as TextView).error =
                "Please Choose some resource"
            return
        }
        if (selectedDate == getString(R.string.choose_date_TV)) {
            addTransferDate.error = "Please select date"
            return
        }

        sourceAccountPathToSaveData()
        targetAccountPathToSaveData()

        findNavController().navigate(R.id.action_transferFragment_to_homeFragment)

    }

    /**
     * saving the details in the source account
     *
     * for the source account the transferred amount is considered as expense
     * so the transferred amount will be saved as expense under the chosen
     * category.
     */


    private fun sourceAccountPathToSaveData() {
        val userId: String =
            ref.push().key.toString()//push will generate unique key for every users

        val index = selectedDate.lastIndexOf('/')
        val date = selectedDate.substring(0, index)
        val path = "Expense/$date/$sourceAccount/$userId"

        val user =
            Expense(
                userId,
                transferredAmt,
                selectedDate,
                sourceAccount,
                targetAccount,
                "Transferred Amount from $sourceAccount to $targetAccount",
                timeStamp
            )

        ref.child(path).setValue(user).addOnCompleteListener {
            Toast.makeText(activity, "Details saved successfully", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * saving the details in the target account
     *
     * for the target account the transferred amount is considered as income
     * so the transferred amount will be saved as income under the chosen
     * category.
     */

    private fun targetAccountPathToSaveData() {

        val userId: String =
            ref.push().key.toString()//push will generate unique key for every users

        val index = selectedDate.lastIndexOf('/')
        val date = selectedDate.substring(0, index)
        val path = "Income/$date/$targetAccount/$userId"

        val user =
            Income(
                userId,
                transferredAmt,
                selectedDate,
                sourceAccount,
                targetAccount,
                timeStamp
            )

        ref.child(path).setValue(user).addOnCompleteListener {
            Toast.makeText(activity, "Details saved successfully", Toast.LENGTH_SHORT).show()
        }


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
            saveTransferDetails()
        }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int ->
        }
        builder.show()
    }


}