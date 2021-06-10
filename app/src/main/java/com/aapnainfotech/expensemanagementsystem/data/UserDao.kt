package com.aapnainfotech.expensemanagementsystem.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDetails(user : User)

    @Query("SELECT * FROM user_table ORDER BY rId Asc ")
    fun readAllData() : LiveData<List<User>>

}