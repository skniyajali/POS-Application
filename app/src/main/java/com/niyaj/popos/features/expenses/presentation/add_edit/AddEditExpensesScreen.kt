package com.niyaj.popos.features.expenses.presentation.add_edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialApi::class)
@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditExpensesScreen(
    expensesId: String? = "",
    navController: NavController = rememberNavController(),
    addEditExpensesViewModel: AddEditExpensesViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {

    val expensesCategories = addEditExpensesViewModel.expensesCategories.collectAsState().value.expensesCategory

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val expensesCategoryName = addEditExpensesViewModel.addEditState.collectAsState().value.expensesCategory.expensesCategoryName
    val expensesCategoryError = addEditExpensesViewModel.addEditState.collectAsState().value.expensesCategoryError

    var categoryDropdownToggled by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        addEditExpensesViewModel.eventFlow.collectLatest { event ->
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
        text = if (!expensesId.isNullOrEmpty())
            stringResource(id = R.string.update_expense)
        else
            stringResource(id = R.string.add_new_expense),
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            ExposedDropdownMenuBox(
                expanded = expensesCategories.isNotEmpty() && categoryDropdownToggled,
                onExpandedChange = {
                    categoryDropdownToggled = !categoryDropdownToggled
                }
            ) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            //This value is used to assign to the DropDown the same width
                            textFieldSize = coordinates.size.toSize()
                        },
                    text = expensesCategoryName,
                    hint = "Expenses Type/Name",
                    error = expensesCategoryError,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = categoryDropdownToggled
                        )
                    },
                )

                DropdownMenu(
                    expanded = expensesCategories.isNotEmpty() && categoryDropdownToggled,
                    onDismissRequest = {
                        categoryDropdownToggled = false
                    },
                    modifier = Modifier
                        .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                ) {
                    expensesCategories.forEachIndexed{ index, expensesCategory ->
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                addEditExpensesViewModel.onExpensesEvent(
                                    AddEditExpensesEvent.ExpensesCategoryNameChanged(
                                        expensesCategory.expensesCategoryName,
                                        expensesCategory.expensesCategoryId
                                    )
                                )
                                categoryDropdownToggled = false
                            }
                        ) {
                            Text(
                                text = expensesCategory.expensesCategoryName,
                                style = MaterialTheme.typography.body1,
                            )
                        }

                        if(index != expensesCategories.size - 1) {
                            Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                text = addEditExpensesViewModel.addEditState.collectAsState().value.expensesPrice,
                hint = "Expenses Amount",
                keyboardType = KeyboardType.Number,
                error = addEditExpensesViewModel.addEditState.collectAsState().value.expensesPriceError,
                onValueChange = {
                    addEditExpensesViewModel.onExpensesEvent(
                        AddEditExpensesEvent.ExpensesPriceChanged(
                            it
                        )
                    )
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                text = addEditExpensesViewModel.addEditState.collectAsState().value.expensesRemarks,
                hint = "Payment Note/Remarks",
                error = null,
                onValueChange = {
                    addEditExpensesViewModel.onExpensesEvent(
                        AddEditExpensesEvent.ExpensesRemarksChanged(
                            it
                        )
                    )
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Button(
                onClick = {
                    if (!expensesId.isNullOrEmpty()) {
                        addEditExpensesViewModel.onExpensesEvent(
                            AddEditExpensesEvent.UpdateExpenses(
                                expensesId
                            )
                        )
                    } else {
                        addEditExpensesViewModel.onExpensesEvent(AddEditExpensesEvent.CreateNewExpenses)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(44.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = if (!expensesId.isNullOrEmpty())
                        stringResource(id = R.string.update_expense).uppercase()
                    else
                        stringResource(id = R.string.add_new_expense).uppercase(),
                    style = MaterialTheme.typography.button,
                )
            }

        }
    }
}