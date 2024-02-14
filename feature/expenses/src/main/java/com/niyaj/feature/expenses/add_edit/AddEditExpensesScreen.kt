package com.niyaj.feature.expenses.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.automirrored.filled.SpeakerNotes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Paid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.ExpenseTestTags.ADD_EDIT_EXPENSE_BUTTON
import com.niyaj.common.tags.ExpenseTestTags.CREATE_NEW_EXPENSE
import com.niyaj.common.tags.ExpenseTestTags.EDIT_EXPENSE_ITEM
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_AMOUNT_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_AMOUNT_FIELD
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_DATE_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_DATE_FIELD
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_FIELD
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NOTE_FIELD
import com.niyaj.common.utils.getCalculatedStartDate
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toSalaryDate
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.CustomDropdownMenuItem
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

/**
 * Add/Edit Expenses Screen
 * @author Sk Niyaj Ali
 * @param expensesId
 * @param navController
 * @param viewModel
 * @param resultBackNavigator
 * @see AddEditExpensesViewModel
 */
@OptIn(ExperimentalMaterialApi::class)
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditExpensesScreen(
    expensesId: String = "",
    navController: NavController = rememberNavController(),
    viewModel: AddEditExpensesViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()

    val dateError = viewModel.dateError.collectAsStateWithLifecycle().value
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val amountError = viewModel.priceError.collectAsStateWithLifecycle().value

    val enableBtn = listOf(dateError, nameError, amountError).all { it == null }

    val title = if (expensesId.isEmpty()) CREATE_NEW_EXPENSE else EDIT_EXPENSE_ITEM

    val category = viewModel.state.expensesCategory

    val expensesCategories = viewModel.expensesName.collectAsStateWithLifecycle().value

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    var categoryDropdownToggled by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
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

    BottomSheetWithCloseDialog(
        modifier = Modifier
            .fillMaxWidth(),
        text = title,
        icon = Icons.Default.Paid,
        onClosePressed = { navController.navigateUp() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(EXPENSE_NAME_FIELD) {
                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = categoryDropdownToggled,
                    onExpandedChange = {
                        categoryDropdownToggled = !categoryDropdownToggled
                    }
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(EXPENSE_NAME_FIELD)
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = category.expensesCategoryName,
                        leadingIcon = Icons.Default.Group,
                        label = EXPENSE_NAME_FIELD,
                        error = nameError,
                        errorTag = EXPENSE_NAME_ERROR,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownToggled)
                        },
                    )

                    DropdownMenu(
                        expanded = categoryDropdownToggled,
                        onDismissRequest = {
                            categoryDropdownToggled = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                    ) {
                        expensesCategories.forEach { expensesCategory ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                enabled = category.expensesCategoryId != expensesCategory.expensesCategoryId,
                                onClick = {
                                    viewModel.onEvent(
                                        AddEditExpenseEvent.ExpensesNameChanged(
                                            expensesCategory
                                        )
                                    )
                                    categoryDropdownToggled = false
                                }
                            ) {
                                TextWithIcon(
                                    text = expensesCategory.expensesCategoryName,
                                    icon = Icons.Default.Category
                                )
                            }

                            Divider(modifier = Modifier.fillMaxWidth())
                        }

                        if (expensesCategories.isEmpty()) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                enabled = false,
                                onClick = {},
                                content = {
                                    Text(
                                        text = "Expenses category is not available",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                },
                            )
                        }

                        CustomDropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                navController.navigate(Screens.EXPENSES_CATEGORY_SCREEN)
                            },
                            text = {
                                Text(
                                    text = "Create new category",
                                    color = MaterialTheme.colors.secondary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Create",
                                    tint = MaterialTheme.colors.secondary
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                                    contentDescription = "trailing"
                                )
                            }
                        )
                    }
                }
            }

            item(EXPENSE_DATE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(EXPENSE_DATE_FIELD),
                    text = viewModel.state.expenseDate.toSalaryDate,
                    label = EXPENSE_DATE_FIELD,
                    leadingIcon = Icons.Default.CalendarToday,
                    error = dateError,
                    errorTag = EXPENSE_DATE_ERROR,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { dialogState.show() },
                            modifier = Modifier.testTag("Given Date")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Given Date"
                            )
                        }
                    }
                )
            }

            item(EXPENSE_AMOUNT_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(EXPENSE_AMOUNT_FIELD),
                    text = viewModel.state.expenseAmount,
                    label = EXPENSE_AMOUNT_FIELD,
                    leadingIcon = Icons.Default.Money,
                    keyboardType = KeyboardType.Number,
                    error = amountError,
                    errorTag = EXPENSE_AMOUNT_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditExpenseEvent.ExpensesAmountChanged(it))
                    },
                )
            }

            item(EXPENSE_NOTE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(EXPENSE_NOTE_FIELD),
                    text = viewModel.state.expenseNote,
                    label = EXPENSE_NOTE_FIELD,
                    leadingIcon = Icons.AutoMirrored.Filled.SpeakerNotes,
                    error = null,
                    onValueChange = {
                        viewModel.onEvent(AddEditExpenseEvent.ExpensesNoteChanged(it))
                    },
                )
            }

            item(ADD_EDIT_EXPENSE_BUTTON) {
                Spacer(modifier = Modifier.height(SpaceMini))

                StandardButtonFW(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ADD_EDIT_EXPENSE_BUTTON),
                    text = title,
                    enabled = enableBtn,
                    icon = if (expensesId.isNotEmpty()) Icons.Default.Edit else Icons.Default.Add,
                    onClick = {
                        viewModel.onEvent(AddEditExpenseEvent.AddOrUpdateExpense(expensesId))
                    },
                )
            }
        }
    }

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker(
            allowedDateValidator = { date ->
                date.toMilliSecond >= getCalculatedStartDate("-7") && (date <= LocalDate.now())
            }
        ) { date ->
            viewModel.onEvent(AddEditExpenseEvent.ExpensesDateChanged(date.toMilliSecond))
        }
    }
}