package com.aapnainfotech.expensemanagementsystem.model

class Income(val id : String,
             val income : String,
             val date : String,
             val category : String,
             val source : String,
             val timeStamp : String) {

    /**
     * when we are reading values from the firebase database
     * we need to define and empty constructor in
     *our model class
     */

    constructor(): this("","","","","",""){

    }


}