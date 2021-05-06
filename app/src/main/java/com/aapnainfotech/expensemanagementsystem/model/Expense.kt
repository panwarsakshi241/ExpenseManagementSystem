package com.aapnainfotech.expensemanagementsystem.model

import java.security.Timestamp

class Expense(val id : String,
              val expense : String,
              val date : String,
              val category : String,
              val source : String,
              val details : String,
              val timeStamp :  String ) {

    /**
     * when we are reading values from the firebase database
     * we need to define and empty constructor in
     *our model class
     */

    constructor(): this("","","","","","",""){}

}