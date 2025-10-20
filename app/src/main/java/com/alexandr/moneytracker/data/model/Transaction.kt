package com.alexandr.moneytracker.data.model

import androidx.room.*
import java.io.Serializable

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = TransactionType::class,
        parentColumns = ["id"],
        childColumns = ["typeId"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val amount: Double,
    val description: String,
    val typeId: Int,
    val date: Long
) : Serializable
