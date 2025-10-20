package com.alexandr.moneytracker.data.dao

import androidx.room.*
import com.alexandr.moneytracker.data.model.Transaction
import com.alexandr.moneytracker.data.model.TransactionDirection

// Вспомогательный класс
data class TransactionWithDirection(
    @Embedded val transaction: Transaction,
    val direction: TransactionDirection?
)

@Dao
interface TransactionDao {
    @Query("""
    SELECT transactions.*, transaction_types.direction 
    FROM transactions
    LEFT JOIN transaction_types ON transactions.typeId = transaction_types.id
    ORDER BY transactions.date DESC
""")
    fun getAllTransactionsWithDirection(): List<TransactionWithDirection>

    @Insert
    fun insertAll(vararg transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Update
    fun update(vararg transaction: Transaction)

    @Query("SELECT COUNT(*) FROM transactions WHERE typeId = :typeId")
    suspend fun countTransactionsWithType(typeId: Int): Int

}

