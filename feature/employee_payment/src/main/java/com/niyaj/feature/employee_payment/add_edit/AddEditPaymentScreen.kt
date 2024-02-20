package com.niyaj.feature.employee_payment.add_edit

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
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person4
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.PaymentScreenTags.ADD_EDIT_PAYMENT_ENTRY_BUTTON
import com.niyaj.common.tags.PaymentScreenTags.ADD_EDIT_PAYMENT_SCREEN
import com.niyaj.common.tags.PaymentScreenTags.CREATE_NEW_PAYMENT
import com.niyaj.common.tags.PaymentScreenTags.EDIT_PAYMENT_ITEM
import com.niyaj.common.tags.PaymentScreenTags.GIVEN_AMOUNT_ERROR
import com.niyaj.common.tags.PaymentScreenTags.GIVEN_AMOUNT_FIELD
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_DATE_ERROR
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_DATE_FIELD
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_EMPLOYEE_NAME_ERROR
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_EMPLOYEE_NAME_FIELD
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_MODE_ERROR
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_MODE_FIELD
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_NOTE_ERROR
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_NOTE_FIELD
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_TYPE_ERROR
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_TYPE_FIELD
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toSafeAmount
import com.niyaj.common.utils.toSalaryDate
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import com.niyaj.ui.components.CustomDropdownMenuItem
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

/**
 * Add/Edit Salary Screen
 * @author Sk Niyaj Ali
 * @param paymentId
 * @param employeeId
 * @param navController
 * @param viewModel
 * @param resultBackNavigator
 * @see AddEditPaymentViewModel
 */
@OptIn(ExperimentalMaterialApi::class)
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditPaymentScreen(
    paymentId: String = "",
    employeeId: String = "",
    navController: NavController = rememberNavController(),
    viewModel: AddEditPaymentViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val dialogState = rememberMaterialDialogState()
    val lazyListState = rememberLazyListState()

    val employees = viewModel.employees.collectAsStateWithLifecycle().value

    val employeeError = viewModel.employeeError.collectAsStateWithLifecycle().value
    val amountError = viewModel.amountError.collectAsStateWithLifecycle().value
    val dateError = viewModel.dateError.collectAsStateWithLifecycle().value
    val typeError = viewModel.paymentTypeError.collectAsStateWithLifecycle().value
    val modeError = viewModel.paymentModeError.collectAsStateWithLifecycle().value
    val noteError = viewModel.paymentNoteError.collectAsStateWithLifecycle().value

    val enableBtn = listOf(
        employeeError,
        amountError,
        dateError,
        modeError,
        typeError,
        noteError,
    ).all { it == null }

    val title = if (paymentId.isEmpty()) CREATE_NEW_PAYMENT else EDIT_PAYMENT_ITEM
    val selectedEmployee = viewModel.selectedEmployee.collectAsStateWithLifecycle().value

    var employeeToggled by remember { mutableStateOf(false) }

    var paymentTypeExpanded by remember { mutableStateOf(false) }

    var paymentModeExpanded by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

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

    BottomSheetWithCloseDialog(
        modifier = Modifier
            .testTag(ADD_EDIT_PAYMENT_SCREEN)
            .fillMaxWidth(),
        text = title,
        icon = Icons.Default.Payments,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            item(PAYMENT_EMPLOYEE_NAME_FIELD) {
                ExposedDropdownMenuBox(
                    expanded = employees.isNotEmpty() && employeeToggled,
                    onExpandedChange = {
                        employeeToggled = !employeeToggled
                    },
                    modifier = Modifier.testTag(PAYMENT_EMPLOYEE_NAME_FIELD)
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = selectedEmployee.employeeName,
                        label = PAYMENT_EMPLOYEE_NAME_FIELD,
                        leadingIcon = Icons.Default.Person4,
                        error = employeeError,
                        errorTag = PAYMENT_EMPLOYEE_NAME_ERROR,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = employeeToggled)
                        },
                    )

                    DropdownMenu(
                        expanded = employees.isNotEmpty() && employeeToggled,
                        onDismissRequest = {
                            employeeToggled = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                    ) {
                        employees.forEachIndexed { index, employee ->
                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(employee.employeeName)
                                    .fillMaxWidth(),
                                onClick = {
                                    viewModel.onEvent(AddEditPaymentEvent.OnSelectEmployee(employee))
                                    employeeToggled = false
                                }
                            ) {
                                Text(
                                    text = employee.employeeName,
                                    style = MaterialTheme.typography.body1,
                                )
                            }

                            if (index != employees.size - 1) {
                                Divider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.Gray,
                                    thickness = 0.8.dp
                                )
                            }
                        }

                        if (employees.isEmpty()) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                enabled = false,
                                onClick = {},
                                content = {
                                    Text(
                                        text = "Employees not available",
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
                                navController.navigate(Screens.ADD_EDIT_EMPLOYEE_SCREEN)
                            },
                            text = {
                                Text(
                                    text = "Create a new employee",
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

            item(PAYMENT_TYPE_FIELD) {
                ExposedDropdownMenuBox(
                    expanded = paymentTypeExpanded,
                    onExpandedChange = {
                        paymentTypeExpanded = !paymentTypeExpanded
                    },
                    modifier = Modifier.testTag(PAYMENT_TYPE_FIELD)
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = viewModel.state.paymentType.name,
                        error = typeError,
                        errorTag = PAYMENT_TYPE_ERROR,
                        label = PAYMENT_TYPE_FIELD,
                        leadingIcon = Icons.AutoMirrored.Filled.MergeType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = paymentTypeExpanded
                            )
                        },
                    )
                    DropdownMenu(
                        expanded = paymentTypeExpanded,
                        onDismissRequest = {
                            paymentTypeExpanded = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                    ) {
                        PaymentType.entries.forEach { paymentType ->
                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(paymentType.name)
                                    .fillMaxWidth(),
                                onClick = {
                                    viewModel.onEvent(
                                        AddEditPaymentEvent.PaymentTypeChanged(paymentType)
                                    )
                                    paymentTypeExpanded = false
                                }
                            ) {
                                Text(
                                    text = paymentType.name,
                                    style = MaterialTheme.typography.body1,
                                )
                            }

                            Divider(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Gray,
                                thickness = 0.8.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(PAYMENT_DATE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(PAYMENT_DATE_FIELD),
                    text = viewModel.state.paymentDate.toSalaryDate,
                    label = PAYMENT_DATE_FIELD,
                    leadingIcon = Icons.Default.CalendarToday,
                    error = dateError,
                    errorTag = PAYMENT_DATE_ERROR,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { dialogState.show() },
                            modifier = Modifier.testTag(PAYMENT_DATE_FIELD)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Given Date"
                            )
                        }
                    }
                )
            }

            item(PAYMENT_MODE_FIELD) {
                ExposedDropdownMenuBox(
                    expanded = paymentModeExpanded,
                    onExpandedChange = {
                        paymentModeExpanded = !paymentModeExpanded
                    },
                    modifier = Modifier.testTag(PAYMENT_MODE_FIELD)
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = viewModel.state.paymentMode.name,
                        error = modeError,
                        errorTag = PAYMENT_MODE_ERROR,
                        label = PAYMENT_MODE_FIELD,
                        leadingIcon = Icons.Default.Payments,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = paymentModeExpanded
                            )
                        },
                    )

                    DropdownMenu(
                        expanded = paymentModeExpanded,
                        onDismissRequest = {
                            paymentModeExpanded = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                    ) {
                        PaymentMode.entries.forEach { paymentMode ->
                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(paymentMode.name)
                                    .fillMaxWidth(),
                                onClick = {
                                    viewModel.onEvent(
                                        AddEditPaymentEvent.PaymentModeChanged(
                                            paymentMode
                                        )
                                    )
                                    paymentModeExpanded = false
                                }
                            ) {
                                Text(
                                    text = paymentMode.name,
                                    style = MaterialTheme.typography.body1,
                                )
                            }

                            Divider(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Gray,
                                thickness = 0.8.dp
                            )
                        }
                    }
                }
            }

            item(GIVEN_AMOUNT_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(GIVEN_AMOUNT_FIELD),
                    text = viewModel.state.paymentAmount,
                    label = GIVEN_AMOUNT_FIELD,
                    leadingIcon = Icons.Default.Money,
                    error = amountError,
                    errorTag = GIVEN_AMOUNT_ERROR,
                    keyboardType = KeyboardType.Number,
                    onValueChange = {
                        viewModel.onEvent(AddEditPaymentEvent.PaymentAmountChanged(it.toSafeAmount))
                    },
                )
            }

            item(PAYMENT_NOTE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(PAYMENT_NOTE_FIELD),
                    text = viewModel.state.paymentNote,
                    label = PAYMENT_NOTE_FIELD,
                    leadingIcon = Icons.Default.Description,
                    error = noteError,
                    errorTag = PAYMENT_NOTE_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditPaymentEvent.PaymentNoteChanged(it))
                    },
                )
            }

            item(ADD_EDIT_PAYMENT_ENTRY_BUTTON) {
                Spacer(modifier = Modifier.height(SpaceMini))

                StandardButtonFW(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON),
                    text = title,
                    enabled = enableBtn,
                    icon = if (paymentId.isNotEmpty()) Icons.Default.Edit else Icons.Default.Add,
                    onClick = {
                        viewModel.onEvent(AddEditPaymentEvent.CreateOrUpdatePayment)
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
                if (selectedEmployee.employeeId.isNotEmpty()) {
                    (date.toMilliSecond >= selectedEmployee.employeeJoinedDate) && (date <= LocalDate.now())
                } else date == LocalDate.now()
            }
        ) { date ->
            viewModel.onEvent(AddEditPaymentEvent.PaymentDateChanged(date.toMilliSecond))
        }
    }
}