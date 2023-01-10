package com.niyaj.popos.features.expenses.presentation.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.SpeakerNotes
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.IconSizeExtraLarge
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButton
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.destinations.AddEditExpensesCategoryScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditExpensesScreen(
    expensesId: String? = "",
    navController: NavController = rememberNavController(),
    addEditExpensesViewModel: AddEditExpensesViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {

    val expensesCategories = addEditExpensesViewModel.expensesCategories.collectAsStateWithLifecycle().value.expensesCategory
    val expensesCategoryName = addEditExpensesViewModel.addEditState.value.expensesCategory.expensesCategoryName
    val expensesCategoryError = addEditExpensesViewModel.addEditState.value.expensesCategoryError

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

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
        icon = Icons.Default.Paid,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ){
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
                        leadingIcon = Icons.Default.Group,
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

                Spacer(modifier = Modifier.width(SpaceSmall))

                IconButton(
                    onClick = {
                        navController.navigate(AddEditExpensesCategoryScreenDestination())
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddBox,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .size(IconSizeExtraLarge)
                    )
                }
            }



            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                text = addEditExpensesViewModel.addEditState.value.expensesPrice,
                hint = "Expenses Amount",
                leadingIcon = Icons.Default.Money,
                keyboardType = KeyboardType.Number,
                error = addEditExpensesViewModel.addEditState.value.expensesPriceError,
                onValueChange = {
                    addEditExpensesViewModel.onExpensesEvent(
                        AddEditExpensesEvent.ExpensesPriceChanged(it)
                    )
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                text = addEditExpensesViewModel.addEditState.value.expensesRemarks,
                hint = "Payment Note/Remarks",
                leadingIcon = Icons.Default.SpeakerNotes,
                error = null,
                onValueChange = {
                    addEditExpensesViewModel.onExpensesEvent(
                        AddEditExpensesEvent.ExpensesRemarksChanged(it)
                    )
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardButton(
                text = if (!expensesId.isNullOrEmpty()) stringResource(id = R.string.update_expense)
                    else stringResource(id = R.string.add_new_expense),
                icon = if (!expensesId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.Add,
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
            )
        }
    }
}