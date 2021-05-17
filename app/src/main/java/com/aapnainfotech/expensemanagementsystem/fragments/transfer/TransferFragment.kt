package com.aapnainfotech.expensemanagementsystem.fragments.transfer

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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class TransferFragment : Fragment() {

    lateinit var addTransferDate: TextView
    lateinit var transferButton: Button
    lateinit var ref: DatabaseReference
    lateinit var transferedAmountET: EditText
    lateinit var sourceAccountSpinner: Spinner
    lateinit var targetAccountSpinner: Spinner

    var timeStamp = ""
    var user: String? = ""
    var sourceAccount = ""
    var targetAccount = ""
    var selectedDate = ""
    var transferedAmt = ""


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
        transferedAmountET = view.findViewById(R.id.transferedAmountET)

        user = MainActivity.currentUser?.replace(".", "")
        ref = FirebaseDatabase.getInstance().getReference(user!!)

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

                addTransferDate.setText("$mYear/$month/$mDate")
            }, Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )

        datepickerDialogue.show()

    }

    //retrieve spinner value
    fun sourceAccountSpinnerValue() {

        sourceAccountSpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val CategoryArray = resources.getStringArray(R.array.category)
                sourceAccount = CategoryArray.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }

    fun targetAccountSpinnerValue() {

        targetAccountSpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val CategoryArray = resources.getStringArray(R.array.category)
                targetAccount = CategoryArray.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }

    private fun saveTransferDetails() {

        transferedAmt = transferedAmountET.text.toString().trim()
        selectedDate = addTransferDate.text.toString().trim()

        if (transferedAmt.isEmpty()) {
            transferedAmountET.error = "Please enter Income !"
            return
        }

        if (sourceAccount.equals(getString(R.string.select))) {
            (sourceAccountSpinner.getSelectedView() as TextView).error =
                "Please Choose some category"
            return
        }

        if (targetAccount.equals(getString(R.string.select))) {
            (targetAccountSpinner.getSelectedView() as TextView).error =
                "Please Choose some resource"
            return
        }
        if (selectedDate.equals(getString(R.string.choose_date_TV))) {
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
     * for the source account the transfered amount is considered as expense
     * so the transfered amount will be saved as expense under the choosen
     * category.
     */


    fun sourceAccountPathToSaveData() {
        val userId: String =
            ref.push().key.toString()//push will generate unique key for every users

        val index = selectedDate.lastIndexOf('/')
        val date = selectedDate.substring(0, index)
        val path = "Expense/$date/$sourceAccount/$userId"

        val user =
            Expense(
                userId,
                transferedAmt,
                selectedDate,
                sourceAccount,
                targetAccount,
                "Transfered Amount from $sourceAccount to $targetAccount",
                timeStamp
            )

        ref.child(path).setValue(user).addOnCompleteListener {
            Toast.makeText(activity, "Details saved successfully", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * saving the details in the target account
     *
     * for the target account the transfered amount is considered as income
     * so the transfered amount will be saved as income under the choosen
     * category.
     */

    fun targetAccountPathToSaveData() {

        val userId: String =
            ref.push().key.toString()//push will generate unique key for every users

        val index = selectedDate.lastIndexOf('/')
        val date = selectedDate.substring(0, index)
        val path = "Income/$date/$targetAccount/$userId"

        val user =
            Income(
                userId,
                transferedAmt,
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