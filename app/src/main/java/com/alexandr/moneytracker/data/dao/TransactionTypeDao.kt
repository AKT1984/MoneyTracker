package com.alexandr.moneytracker.data.dao

import androidx.room.*
import com.alexandr.moneytracker.data.model.TransactionType

@Dao
interface TransactionTypeDao {

    @Query("SELECT * FROM transaction_types")
    suspend fun getAll(): List<TransactionType>

    @Query("SELECT * FROM transaction_types WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): TransactionType?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg types: TransactionType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(type: TransactionType)

    @Delete
    suspend fun delete(type: TransactionType)

}

