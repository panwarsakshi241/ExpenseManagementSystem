package com.aapnainfotech.expensemanagementsystem.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.model.Expense

class MyAdapter(val mctx : Context,
                val layoutResId : Int,
                val expenseList : List<Expense>) : ArrayAdapter<Expense>(mctx , layoutResId , expenseList ) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater =  LayoutInflater.from(mctx)

        val view : View = layoutInflater.inflate(layoutResId , null)

        val Expense = view.findViewById<TextView>(R.id.ExpenseEntered)
        val Id = view.findViewById<TextView>(R.id.ExpenseIdFetched)
        val Category = view.findViewById<TextView>(R.id.expenseCategoryFetched)
        val Source = view.findViewById<TextView>(R.id.expenseSourceFetched)
        val Date = view.findViewById<TextView>(R.id.expenseDateFetched)
        val Details = view.findViewById<TextView>(R.id.expenseDetailsFetched)

        val expense = expenseList[position]

        Expense.text = expense.expense
        Id.text = expense.id
        Category.text = expense.category
        Source.text = expense.source
        Date.text = expense.date
        Details.text = expense.details

        return view
    }


}