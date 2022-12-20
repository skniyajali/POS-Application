package com.niyaj.popos.features.expenses.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StartIconButton
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import timber.log.Timber

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun ExpensesSettingScreen(
    navController: NavController = rememberNavController(),
    expensesViewModel: ExpensesViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    val deleteLastSevenDaysState = rememberMaterialDialogState()
    val deleteAllOrdersState = rememberMaterialDialogState()

    LaunchedEffect(key1 = true) {
        expensesViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {
                    Timber.d("Loading.. ${event.isLoading.toString()}")
                }
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = "Expenses Settings",
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalAlignment = Alignment.Start
        ) {
            MaterialDialog(
                dialogState = deleteLastSevenDaysState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            expensesViewModel.onExpensesEvent(ExpensesEvent.DeletePastExpenses)
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
                title(text = "Delete Past Expenses?")
                message(res = R.string.delete_past_expenses_msg)
            }

            MaterialDialog(
                dialogState = deleteAllOrdersState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            expensesViewModel.onExpensesEvent(ExpensesEvent.DeleteAllExpenses)
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
                title(text = "Delete All Expenses?")
                message(res = R.string.delete_all_expenses_msg)
            }

            StartIconButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    deleteLastSevenDaysState.show()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Past Expenses",
                    tint = MaterialTheme.colors.secondaryVariant
                )
                Text(
                    text = "Delete Past Expenses",
                    style = MaterialTheme.typography.button,
                )
            }
            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))
            StartIconButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    deleteAllOrdersState.show()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Delete All Expenses",
                    tint = MaterialTheme.colors.error
                )
                Text(
                    text = "Delete All Expenses",
                    style = MaterialTheme.typography.button,
                )
            }
        }
    }
}