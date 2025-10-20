package com.alexandr.moneytracker.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alexandr.moneytracker.data.dao.TransactionDao
import com.alexandr.moneytracker.data.dao.TransactionTypeDao
import com.alexandr.moneytracker.data.model.*
import com.alexandr.moneytracker.data.model.Transaction
import kotlinx.coroutines.*

@Database(entities = [Transaction::class, TransactionType::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao() : TransactionDao
    abstract fun transactionTypeDao() : TransactionTypeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context : Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "transactions"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db : SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).transactionTypeDao().insertAll(
                                    TransactionType(
                                        label = "Salary",
                                        direction = TransactionDirection.INCOME,
                                    ),
                                    TransactionType(
                                        label = "Groceries",
                                        direction = TransactionDirection.EXPENSE,
                                    )
                                )
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}