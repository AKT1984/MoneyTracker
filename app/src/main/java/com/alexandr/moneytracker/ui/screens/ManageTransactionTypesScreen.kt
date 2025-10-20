package com.alexandr.moneytracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alexandr.moneytracker.data.model.TransactionDirection
import com.alexandr.moneytracker.data.model.TransactionType
import com.alexandr.moneytracker.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@Composable
fun ManageTransactionTypesScreen(
    viewModel: TransactionViewModel,
    onFinished: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    val types by viewModel.transactionTypes.collectAsState()
    var label by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf(TransactionDirection.EXPENSE) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                Text("Manage Categories", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Category Name") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    RadioButton(
                        selected = direction == TransactionDirection.INCOME,
                        onClick = { direction = TransactionDirection.INCOME }
                    )
                    Text("Income", modifier = Modifier.padding(start = 8.dp))

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = direction == TransactionDirection.EXPENSE,
                        onClick = { direction = TransactionDirection.EXPENSE }
                    )
                    Text("Expense", modifier = Modifier.padding(start = 8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    if (label.isNotBlank()) {
                        viewModel.addTransactionType(
                            TransactionType(label = label, direction = direction)
                        )
                        label = ""
                        scope.launch {
                            snackbarHostState.showSnackbar("Category added")
                        }
                    }
                }) {
                    Text("Add Category")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Existing Categories", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                types.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(type.label)
                            Text(
                                type.direction.name.lowercase()
                                    .replaceFirstChar { it.uppercase() }
                            )
                        }
                        Button(onClick = {
                            viewModel.deleteTransactionType(type) { success ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (success)
                                            "Category deleted"
                                        else
                                            "Cannot delete: category in use."
                                    )
                                }
                            }
                        }) {
                            Text("Delete")
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onFinished() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done")
                }
            }
        }
    }
}
