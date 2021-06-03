package com.aapnainfotech.expensemanagementsystem.model

class Account(val id : String ,
              val amount : Double ,
              val category : String ,
              val timeStamp : String) {

    /**
     * when we are reading values from the firebase database
     * we need to define and empty constructor in
     *our model class
     */

    constructor(): this("",0.0,"","")



}