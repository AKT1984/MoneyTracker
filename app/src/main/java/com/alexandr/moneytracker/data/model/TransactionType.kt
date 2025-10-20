package com.alexandr.moneytracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionDirection {
    INCOME,
    EXPENSE
}



@Entity(tableName = "transaction_types")
data class TransactionType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val direction: TransactionDirection
)

