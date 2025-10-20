package com.alexandr.moneytracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alexandr.moneytracker.data.dao.TransactionWithDirection
import com.alexandr.moneytracker.data.database.AppDatabase
import com.alexandr.moneytracker.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    var selectedTransaction: Transaction? = null
    private val db = AppDatabase.Companion.getInstance(application)

    private val _transactions = MutableStateFlow<List<TransactionWithDirection>>(emptyList())
    val transactions: StateFlow<List<TransactionWithDirection>> = _transactions

    private val _transactionTypes = MutableStateFlow<List<TransactionType>>(emptyList())
    val transactionTypes: StateFlow<List<TransactionType>> = _transactionTypes

    init {
        loadTransactions()
        loadTransactionTypes()
    }

    private fun loadTransactionTypes() {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = db.transactionTypeDao()
            val existing = dao.getAll()

            if (existing.isEmpty()) {
                dao.insertAll(
                    TransactionType(label = "Salary", direction = TransactionDirection.INCOME),
                    TransactionType(label = "Groceries", direction = TransactionDirection.EXPENSE),
                )
            }

            // Always update the state after checking
            _transactionTypes.value = dao.getAll()
        }
    }
    fun addTransactionType(type: TransactionType) {
        viewModelScope.launch(Dispatchers.IO) {
            db.transactionTypeDao().insert(type)
            loadTransactionTypes()
        }
    }

    fun deleteTransactionType(type: TransactionType, onResult: (success: Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val usageCount = db.transactionDao().countTransactionsWithType(type.id)
            if (usageCount == 0) {
                db.transactionTypeDao().delete(type)
                loadTransactionTypes()
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }


    fun loadTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            _transactions.value = db.transactionDao().getAllTransactionsWithDirection()
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            db.transactionDao().insertAll(transaction)
            loadTransactions()
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            db.transactionDao().update(transaction)
            loadTransactions()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            db.transactionDao().delete(transaction)
            loadTransactions()
        }
    }


}