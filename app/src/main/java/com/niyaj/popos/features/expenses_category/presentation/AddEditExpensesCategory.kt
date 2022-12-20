package com.niyaj.popos.features.expenses_category.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.flow.collectLatest

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditExpensesCategoryScreen(
    expensesCategoryId: String? = "",
    navController: NavController = rememberNavController(),
    expensesCategoryViewModel : ExpensesCategoryViewModel = hiltViewModel (),
    resultBackNavigator: ResultBackNavigator<String>,
) {

    LaunchedEffect(key1 = expensesCategoryId) {
        expensesCategoryViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = if (!expensesCategoryId.isNullOrEmpty())
            stringResource(id = R.string.update_expenses_category)
        else
            stringResource(id = R.string.create_new_expenses_category),
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StandardOutlinedTextField(
                modifier = Modifier,
                text = expensesCategoryViewModel.addEditState.expensesCategoryName,
                hint = "ExpensesCategory Name",
                error = expensesCategoryViewModel.addEditState.expensesCategoryNameError,
                onValueChange = {
                    expensesCategoryViewModel.onExpensesCategoryEvent(
                        ExpensesCategoryEvent.ExpensesCategoryNameChanged(it)
                    )
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Button(
                onClick = {
                    if (!expensesCategoryId.isNullOrEmpty()) {
                        expensesCategoryViewModel.onExpensesCategoryEvent(
                            ExpensesCategoryEvent.UpdateExpensesCategory(expensesCategoryId)
                        )
                    } else {
                        expensesCategoryViewModel.onExpensesCategoryEvent(
                            ExpensesCategoryEvent.CreateNewExpensesCategory
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(ButtonSize),
            ) {
                Text(
                    text = if (!expensesCategoryId.isNullOrEmpty())
                        stringResource(id = R.string.update_expenses_category).uppercase()
                    else
                        stringResource(id = R.string.create_new_expenses_category).uppercase(),
                    style = MaterialTheme.typography.button,
                )
            }
        }
    }
}