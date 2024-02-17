package com.niyaj.feature.expenses.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.ExpenseTestTags.DELETE_ALL_EXPENSE_MESSAGE
import com.niyaj.common.tags.ExpenseTestTags.DELETE_ALL_EXPENSE_TITLE
import com.niyaj.common.tags.ExpenseTestTags.DELETE_PAST_EXPENSE_MESSAGE
import com.niyaj.common.tags.ExpenseTestTags.DELETE_PAST_EXPENSE_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_SETTINGS_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EXPORT_EXPENSE_TITLE
import com.niyaj.common.tags.ExpenseTestTags.IMPORT_EXPENSE_TITLE
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.expenses.ExpensesViewModel
import com.niyaj.feature.expenses.destinations.ExportExpensesScreenDestination
import com.niyaj.feature.expenses.destinations.ImportExpensesScreenDestination
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.event.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch

/**
 * Expenses Setting Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param viewModel
 * @param resultBackNavigator
 * @see ExpensesViewModel
 */
@Destination
@Composable
fun ExpensesSettingScreen(
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: ExpensesSettingViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
    importRecipient: ResultRecipient<ImportExpensesScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportExpensesScreenDestination, String>
) {
    val scope = rememberCoroutineScope()
    val deleteLastSevenDaysState = rememberMaterialDialogState()
    val deleteAllOrdersState = rememberMaterialDialogState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }
            }
        }
    }

    importRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    exportRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }

            is NavResult.Canceled -> {}
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        navActions = {},
        title = {
            Text(text = EXPENSE_SETTINGS_TITLE)
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall)
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            item {
                SettingsCard(
                    text = "Delete Past Expenses",
                    icon = Icons.Default.Delete,
                    onClick = {
                        deleteLastSevenDaysState.show()
                    },
                )
            }

            item {
                SettingsCard(
                    text = "Delete All Expenses",
                    icon = Icons.Default.DeleteForever,
                    onClick = {
                        deleteAllOrdersState.show()
                    },
                )
            }

            item {
                SettingsCard(
                    text = IMPORT_EXPENSE_TITLE,
                    icon = Icons.Default.SaveAlt,
                    onClick = {
                        navController.navigate(ImportExpensesScreenDestination())
                    },
                )
            }

            item {
                SettingsCard(
                    text = EXPORT_EXPENSE_TITLE,
                    icon = Icons.Default.SaveAlt,
                    iconModifier = Modifier.rotate(180F),
                    onClick = {
                        navController.navigate(ExportExpensesScreenDestination())
                    },
                )
            }
        }
    }

    MaterialDialog(
        dialogState = deleteLastSevenDaysState,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = {
                    viewModel.onEvent(ExpensesSettingsEvent.DeletePastExpenses)
                }
            )
            negativeButton(
                text = "Cancel",
                onClick = {
                    deleteLastSevenDaysState.hide()
                },
            )
        }
    ) {
        title(text = DELETE_PAST_EXPENSE_TITLE)
        message(text = DELETE_PAST_EXPENSE_MESSAGE)
    }

    MaterialDialog(
        dialogState = deleteAllOrdersState,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = {
                    viewModel.onEvent(ExpensesSettingsEvent.DeleteAllExpenses)
                }
            )
            negativeButton(
                text = "Cancel",
                onClick = {
                    deleteAllOrdersState.hide()
                },
            )
        }
    ) {
        title(text = DELETE_ALL_EXPENSE_TITLE)
        message(text = DELETE_ALL_EXPENSE_MESSAGE)
    }
}