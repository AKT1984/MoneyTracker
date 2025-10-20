package com.alexandr.moneytracker.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexandr.moneytracker.data.model.Transaction
import com.alexandr.moneytracker.data.model.TransactionDirection
import com.alexandr.moneytracker.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TransactionViewModel,
    onAddClick: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val transactionsState = viewModel.transactions.collectAsState()
    val transactions = transactionsState.value
    val scope = rememberCoroutineScope()

    val budget = transactions.filter { it.direction == TransactionDirection.INCOME }
        .sumOf { it.transaction.amount }
    val expense = transactions.filter { it.direction == TransactionDirection.EXPENSE }
        .sumOf { it.transaction.amount }
    val total = budget - expense

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Total Balance", style = MaterialTheme.typography.labelLarge)
            Text(
                text = "%.2f".format(total),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.2f".format(budget),
                        color = Color(0xFF00C853),
                        fontWeight = FontWeight.Bold
                    )
                    Text("Budget")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.2f".format(expense),
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Expense")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Transactions", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions, key = { it.transaction.id }) { transactionWithDirection ->
                    val transaction = transactionWithDirection.transaction
                    val isIncome = transactionWithDirection.direction == TransactionDirection.INCOME

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                                scope.launch {
                                    viewModel.deleteTransaction(transaction)
                                }
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val direction = dismissState.dismissDirection
                            if (direction != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surface)
                                )
                            }
                        },
                        content = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEditTransaction(transaction) }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Column(modifier = Modifier.weight(1f)) {

                                    Text(transaction.label)
                                    Text(
                                        text = DateUtils.getRelativeTimeSpanString(transaction.date).toString() ,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = (if (isIncome) "+" else "-") + "%.2f".format(abs(transaction.amount)),
                                    color = if (isIncome) Color(0xFF00C853) else Color.Red
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}


