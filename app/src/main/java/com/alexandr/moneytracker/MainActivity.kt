package com.alexandr.moneytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.alexandr.moneytracker.ui.screens.*
import com.alexandr.moneytracker.ui.theme.MoneyTrackerTheme
import com.alexandr.moneytracker.viewmodel.TransactionViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: TransactionViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory(application)
            )
            val navController = rememberNavController()

            MoneyTrackerTheme {
                NavHost(navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            viewModel = viewModel,
                            onAddClick = { navController.navigate("add") },
                            onEditTransaction = { transaction ->
                                viewModel.selectedTransaction = transaction
                                navController.navigate("edit")
                            }
                        )
                    }
                    composable("add") {
                        AddTransactionScreen(
                            viewModel = viewModel,
                            onTransactionAdded = { navController.popBackStack() },
                            onManageTypes = { navController.navigate("manage_types") }
                        )

                    }
                    composable("edit") {
                        val transaction = viewModel.selectedTransaction
                        if (transaction != null) {
                            EditTransactionScreen(
                                transaction = transaction,
                                viewModel = viewModel,
                                onTransactionUpdated = { navController.popBackStack() },
                                onManageTypes = { navController.navigate("manage_types") }

                            )
                        } else {
                            // Optionally show error
                            Text("No transaction selected.")
                        }
                    }

                    composable("manage_types") {
                        ManageTransactionTypesScreen(
                            viewModel = viewModel,
                            onFinished = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}


