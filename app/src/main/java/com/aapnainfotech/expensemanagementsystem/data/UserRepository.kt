package com.aapnainfotech.expensemanagementsystem.data

import androidx.lifecycle.LiveData

class UserRepository(
    private val userDao: UserDao
) {
    val readAllData : LiveData<List<User>> = userDao.readAllData()

    suspend fun addDetails(user : User){
        userDao.addDetails(user)
    }
}