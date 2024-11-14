package com.bignerdranch.andriod.criminalintent.database

import androidx.room.Dao
import androidx.room.Query
import com.bignerdranch.andriod.criminalintent.Crime
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CrimeDao {

   @Query("Select * From crime")
  // suspend fun getCrimes(): List<Crime>
    fun getCrimes(): Flow<List<Crime>>


    @Query("Select * From crime Where id=(:id)")
   suspend fun getCrime(id: UUID):Crime

}