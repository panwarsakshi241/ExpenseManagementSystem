package com.aapnainfotech.expensemanagementsystem.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
class User(
    @PrimaryKey(autoGenerate = true)
    val rId: Int,
    val rYear: String,
    val rMonth: String,
    val rIncome: String,
    val rExpense: String,
    val rBalance: String,
    val rSpinner : String

    )