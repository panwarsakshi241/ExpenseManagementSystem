package com.aapnainfotech.expensemanagementsystem.model

class Budget(val id : String ,
             val amount : String,
             val month : String,
             val timestamp : String) {

    /**
     * when we are reading values from the firebase database
     * we need to define and empty constructor in
     *our model class
     */

    constructor(): this("","","",""){

    }


}