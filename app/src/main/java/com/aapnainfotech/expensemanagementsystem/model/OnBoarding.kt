package com.aapnainfotech.expensemanagementsystem.model

data class OnBoarding(
    val onBoardingImage: Int,
    val date: String,
    val expense: String,
    val source: String,
    val category: String,
    val comment: String
)
