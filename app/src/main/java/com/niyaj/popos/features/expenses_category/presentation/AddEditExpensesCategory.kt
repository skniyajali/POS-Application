package com.niyaj.popos.features.expenses_category.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButtonFW
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.flow.collectLatest

/**
 * Add/Edit Expenses Category Screen
 * @author Sk Niyaj Ali
 * @param expensesCategoryId
 * @param navController
 * @param expensesCategoryViewModel
 * @param resultBackNavigator
 * @see ExpensesCategoryViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
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
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    SentryTraced(tag = "AddEditExpensesCategoryScreen") {
        BottomSheetWithCloseDialog(
            modifier = Modifier.fillMaxWidth(),
            text = if (!expensesCategoryId.isNullOrEmpty())
                stringResource(id = R.string.update_expenses_category)
            else
                stringResource(id = R.string.create_new_expenses_category),
            icon = Icons.Default.Category,
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
                    hint = "Expenses Category Name",
                    leadingIcon = Icons.Default.Category,
                    error = expensesCategoryViewModel.addEditState.expensesCategoryNameError,
                    onValueChange = {
                        expensesCategoryViewModel.onExpensesCategoryEvent(
                            ExpensesCategoryEvent.ExpensesCategoryNameChanged(it)
                        )
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))

                StandardButtonFW(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (!expensesCategoryId.isNullOrEmpty()) stringResource(id = R.string.update_expenses_category)
                    else stringResource(id = R.string.create_new_expenses_category),
                    icon = if (!expensesCategoryId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.Add,
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
                )
            }
        }
    }
}