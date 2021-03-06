package com.aapnainfotech.expensemanagementsystem.adapter.recyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aapnainfotech.expensemanagementsystem.R
import com.aapnainfotech.expensemanagementsystem.model.Expense

class MyRecyclerViewAdapter(
    private val list: List<Expense>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>() {

    //to change the body of created function use file
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.recyclerview_item,
            parent, false
        )

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = list[position]

//        holder.imageView.setImageResource(currentItem.onBoardingImage)
        holder.date.text = currentItem.date
        holder.expense.text = currentItem.expense
        holder.source.text = currentItem.source
        holder.category.text = currentItem.category
        holder.comment.text = currentItem.details
    }

    override fun getItemCount() = list.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        //        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val date: TextView = itemView.findViewById(R.id.tv_date)
        val expense: TextView = itemView.findViewById(R.id.tv_expense)
        val source: TextView = itemView.findViewById(R.id.tv_source)
        val category: TextView = itemView.findViewById(R.id.tv_category)
        val comment: TextView = itemView.findViewById(R.id.tv_details)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val position :Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}