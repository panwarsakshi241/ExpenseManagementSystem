package com.aapnainfotech.expensemanagementsystem.fragments.transfer

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aapnainfotech.expensemanagementsystem.R
import java.util.*


class TransferFragment : Fragment() {

    lateinit var addTransferDate : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transfer, container, false)

        addTransferDate = view.findViewById(R.id.addTransferDateTV)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        addTransferDate.setOnClickListener {
            val datepickerDialogue = DatePickerDialog(requireContext() , DatePickerDialog.OnDateSetListener{ view, mYear, mMonth, mDate ->

                addTransferDate.setText("$mDate / $mMonth / $mYear")
            },year,month,day)

            datepickerDialogue.show()
        }

        return view
    }

}