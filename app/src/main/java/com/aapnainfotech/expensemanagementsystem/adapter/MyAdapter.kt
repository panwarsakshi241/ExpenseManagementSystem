package com.aapnainfotech.expensemanagementsystem.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.model.Expense
import kotlin.math.exp

class MyAdapter(val mctx : Context,
                val layoutResId : Int,
                val expenseList : List<Expense>) : ArrayAdapter<Expense>(mctx , layoutResId , expenseList ) {


    var xpense = ""
    var totExp = 0
    var total = 0
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater =  LayoutInflater.from(mctx)

        val view : View = layoutInflater.inflate(layoutResId , null)

        val Expense = view.findViewById<TextView>(R.id.ExpenseEntered)
        val Id = view.findViewById<TextView>(R.id.ExpenseIdFetched)
        val Category = view.findViewById<TextView>(R.id.expenseCategoryFetched)
        val source = view.findViewById<TextView>(R.id.expenseSourceFetched)
        val date = view.findViewById<TextView>(R.id.expenseDateFetched)
        val details = view.findViewById<TextView>(R.id.expenseDetailsFetched)

        val expense = expenseList[position]

        Expense.text = expense.expense
        Id.text = expense.id
        Category.text = expense.category
        source.text = expense.source
        date.text = expense.date
        details.text = expense.details

        return view
    }


}