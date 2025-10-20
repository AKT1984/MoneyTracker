package com.alexandr.moneytracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alexandr.moneytracker.data.model.Transaction
import com.alexandr.moneytracker.data.model.TransactionType
import com.alexandr.moneytracker.ui.components.SafeScaffold
import com.alexandr.moneytracker.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    onTransactionAdded: () -> Unit,
    onManageTypes: (() -> Unit)

) {
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    var label by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf<TransactionType?>(null) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val transactionTypes by viewModel.transactionTypes.collectAsState()

    val isAmountInvalid = amount.toDoubleOrNull() == null && amount.isNotBlank()
    val isLabelInvalid = label.isBlank()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            focusRequester.requestFocus()
        }
    }

    SafeScaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Label") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    isError = isLabelInvalid,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    colors = OutlinedTextFieldDefaults.colors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    isError = isAmountInvalid,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors()
                )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Type", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))

                    Box {
                        OutlinedButton(
                            onClick = { expanded = !expanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(selectedType?.label ?: "Select Type")
                                Icon(
                                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            transactionTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.label) },
                                    onClick = {
                                        selectedType = type
                                        expanded = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("+ Add/Edit Categories") },
                                onClick = {
                                    expanded = false
                                    onManageTypes.invoke()
                                }
                            )

                        }
                    }
                }

                Button(
                    onClick = {
                        val amt = amount.toDoubleOrNull()
                        if (!isLabelInvalid && amt != null && selectedType != null) {
                            viewModel.addTransaction(
                                Transaction(
                                    label = label,
                                    amount = amt,
                                    description = description,
                                    typeId = selectedType!!.id,
                                    date = System.currentTimeMillis()
                                )
                            )
                            onTransactionAdded()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Transaction")
                }
            }
        }
    }
