package com.niyaj.popos.features.employee_salary.presentation.add_edit

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person4
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButton
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.employee.domain.util.PaymentType
import com.niyaj.popos.features.employee.domain.util.SalaryType
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.ADD_EDIT_PAYMENT_ENTRY_BUTTON
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.GIVEN_AMOUNT_ERROR
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.GIVEN_AMOUNT_FIELD
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.GIVEN_DATE_FIELD
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.PAYMENT_TYPE_ERROR
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.PAYMENT_TYPE_FIELD
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.SALARY_EMPLOYEE_NAME_ERROR
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.SALARY_EMPLOYEE_NAME_FIELD
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.SALARY_NOTE_ERROR
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.SALARY_NOTE_FIELD
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.SALARY_TYPE_ERROR
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.SALARY_TYPE_FIELD
import com.niyaj.popos.utils.toCurrentMilliSecond
import com.niyaj.popos.utils.toMilliSecond
import com.niyaj.popos.utils.toSalaryDate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import io.sentry.compose.SentryTraced
import java.time.LocalDate

/**
 * Add/Edit Salary Screen
 * @author Sk Niyaj Ali
 * @param salaryId
 * @param employeeId
 * @param navController
 * @param addEditSalaryViewModel
 * @param resultBackNavigator
 * @see AddEditSalaryViewModel
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditSalaryScreen(
    salaryId: String = "",
    employeeId: String = "",
    navController: NavController = rememberNavController(),
    addEditSalaryViewModel: AddEditSalaryViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    val employees = addEditSalaryViewModel.employeeState.collectAsStateWithLifecycle().value.employees

    var employeeToggled by remember {
        mutableStateOf(false)
    }

    var salaryTypeExpanded by remember { mutableStateOf(false) }

    var paymentTypeExpanded by remember { mutableStateOf(false) }


    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val dialogState = rememberMaterialDialogState()

    LaunchedEffect(key1 = true) {
        addEditSalaryViewModel.eventFlow.collect { event ->
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

    SentryTraced(tag = "AddEditSalaryScreen-$employeeId") {
        BottomSheetWithCloseDialog(
            modifier = Modifier
                .testTag("AddEdit PaymentEntry Screen")
                .fillMaxWidth(),
            text = if (salaryId.isNotEmpty())
                stringResource(id = R.string.update_salary_entry)
            else
                stringResource(id = R.string.create_salary_entry),
            icon = Icons.Default.Payments,
            onClosePressed = {
                navController.navigateUp()
            }
        ) {
            MaterialDialog(
                dialogState = dialogState,
                buttons = {
                    positiveButton("Ok")
                    negativeButton("Cancel")
                }
            ) {
                datepicker(
                    allowedDateValidator = { date ->
                        if (addEditSalaryViewModel.addEditSalaryState.employee.employeeId.isNotEmpty()) {
                            (date.toMilliSecond >= addEditSalaryViewModel.addEditSalaryState.employee.employeeJoinedDate) && (date <= LocalDate.now())
                        } else date == LocalDate.now()
                    }
                ) {date ->
                    addEditSalaryViewModel.onEvent(
                        AddEditSalaryEvent.SalaryDateChanged(date.toCurrentMilliSecond)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
            ) {
                item {
                    ExposedDropdownMenuBox(
                        expanded = employees.isNotEmpty() && employeeToggled,
                        onExpandedChange = {
                            employeeToggled = !employeeToggled
                        },
                        modifier = Modifier.testTag(SALARY_EMPLOYEE_NAME_FIELD)
                    ) {
                        StandardOutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    //This is used to assign to the DropDown the same width
                                    textFieldSize = coordinates.size.toSize()
                                },
                            text = addEditSalaryViewModel.addEditSalaryState.employee.employeeName,
                            hint = "Employee Name",
                            leadingIcon = Icons.Default.Person4,
                            error = addEditSalaryViewModel.addEditSalaryState.employeeError,
                            errorTag = SALARY_EMPLOYEE_NAME_ERROR,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = employeeToggled
                                )
                            },
                        )

                        DropdownMenu(
                            expanded = employees.isNotEmpty() && employeeToggled,
                            onDismissRequest = {
                                employeeToggled = false
                            },
                            modifier = Modifier
                                .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                        ) {
                            employees.forEachIndexed{ index, employee ->
                                DropdownMenuItem(
                                    modifier = Modifier
                                        .testTag(employee.employeeName)
                                        .fillMaxWidth(),
                                    onClick = {
                                        addEditSalaryViewModel.onEvent(
                                            AddEditSalaryEvent.EmployeeChanged(
                                                employee.employeeId
                                            )
                                        )
                                        employeeToggled = false
                                    }
                                ) {
                                    Text(
                                        text = employee.employeeName,
                                        style = MaterialTheme.typography.body1,
                                    )
                                }

                                if(index != employees.size - 1) {
                                    Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = salaryTypeExpanded,
                        onExpandedChange = {
                            salaryTypeExpanded = !salaryTypeExpanded
                        },
                        modifier = Modifier.testTag(SALARY_TYPE_FIELD)
                    ) {
                        StandardOutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    //This is used to assign to the DropDown the same width
                                    textFieldSize = coordinates.size.toSize()
                                },
                            text = addEditSalaryViewModel.addEditSalaryState.salaryType,
                            error = addEditSalaryViewModel.addEditSalaryState.salaryTypeError,
                            errorTag = SALARY_TYPE_ERROR,
                            hint = "Salary Type",
                            leadingIcon = Icons.Default.MergeType,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = salaryTypeExpanded
                                )
                            },
                        )
                        DropdownMenu(
                            expanded = salaryTypeExpanded,
                            onDismissRequest = {
                                salaryTypeExpanded = false
                            },
                            modifier = Modifier
                                .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                        ) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(SalaryType.Salary.salaryType)
                                    .fillMaxWidth(),
                                onClick = {
                                    addEditSalaryViewModel.onEvent(
                                        AddEditSalaryEvent.SalaryTypeChanged(
                                            SalaryType.Salary.salaryType
                                        )
                                    )
                                    salaryTypeExpanded = false
                                }
                            ) {
                                Text(
                                    text = SalaryType.Salary.salaryType,
                                    style = MaterialTheme.typography.body1,
                                )
                            }

                            Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)

                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(SalaryType.Advanced.salaryType)
                                    .fillMaxWidth(),
                                onClick = {
                                    addEditSalaryViewModel.onEvent(
                                        AddEditSalaryEvent.SalaryTypeChanged(
                                            SalaryType.Advanced.salaryType
                                        )
                                    )
                                    salaryTypeExpanded = false
                                }
                            ) {
                                Text(
                                    text = SalaryType.Advanced.salaryType,
                                    style = MaterialTheme.typography.body1,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }

                item {
                    StandardOutlinedTextField(
                        modifier = Modifier,
                        text = addEditSalaryViewModel.addEditSalaryState.salaryDate.toSalaryDate,
                        hint = "Given Date",
                        leadingIcon = Icons.Default.CalendarToday,
                        error = addEditSalaryViewModel.addEditSalaryState.salaryDateError,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            IconButton(
                                onClick = { dialogState.show() },
                                modifier = Modifier.testTag(GIVEN_DATE_FIELD)
                            ) {
                                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Given Date")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }

                item {
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
                            text = addEditSalaryViewModel.addEditSalaryState.salaryPaymentType,
                            error = addEditSalaryViewModel.addEditSalaryState.salaryPaymentTypeError,
                            errorTag = PAYMENT_TYPE_ERROR,
                            hint = "Payment Type",
                            leadingIcon = Icons.Default.Payments,
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
                                .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                        ) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(PaymentType.Cash.paymentType)
                                    .fillMaxWidth(),
                                onClick = {
                                    addEditSalaryViewModel.onEvent(
                                        AddEditSalaryEvent.PaymentTypeChanged(PaymentType.Cash.paymentType)
                                    )
                                    paymentTypeExpanded = false
                                }
                            ) {
                                Text(
                                    text = PaymentType.Cash.paymentType,
                                    style = MaterialTheme.typography.body1,
                                )
                            }

                            Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)

                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(PaymentType.Online.paymentType)
                                    .fillMaxWidth(),
                                onClick = {
                                    addEditSalaryViewModel.onEvent(
                                        AddEditSalaryEvent.PaymentTypeChanged(PaymentType.Online.paymentType)
                                    )
                                    paymentTypeExpanded = false
                                }
                            ) {
                                Text(
                                    text = PaymentType.Online.paymentType,
                                    style = MaterialTheme.typography.body1,
                                )
                            }

                            Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)

                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(PaymentType.Both.paymentType)
                                    .fillMaxWidth(),
                                onClick = {
                                    addEditSalaryViewModel.onEvent(
                                        AddEditSalaryEvent.PaymentTypeChanged(PaymentType.Both.paymentType)
                                    )
                                    paymentTypeExpanded = false
                                }
                            ) {
                                Text(
                                    text = PaymentType.Both.paymentType,
                                    style = MaterialTheme.typography.body1,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }

                item {
                    StandardOutlinedTextField(
                        modifier = Modifier.testTag(GIVEN_AMOUNT_FIELD),
                        text = addEditSalaryViewModel.addEditSalaryState.salary,
                        hint = "Given Amount",
                        leadingIcon = Icons.Default.Money,
                        error = addEditSalaryViewModel.addEditSalaryState.salaryError,
                        errorTag = GIVEN_AMOUNT_ERROR,
                        keyboardType = KeyboardType.Number,
                        onValueChange = {
                            addEditSalaryViewModel.onEvent(AddEditSalaryEvent.SalaryChanged(it))
                        },
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }

                item {
                    StandardOutlinedTextField(
                        modifier = Modifier.testTag(SALARY_NOTE_FIELD),
                        text = addEditSalaryViewModel.addEditSalaryState.salaryNote,
                        hint = "Salary Note",
                        leadingIcon = Icons.Default.Description,
                        error = addEditSalaryViewModel.addEditSalaryState.salaryNoteError,
                        errorTag = SALARY_NOTE_ERROR,
                        onValueChange = {
                            addEditSalaryViewModel.onEvent(AddEditSalaryEvent.SalaryNoteChanged(it))
                        },
                    )

                    Spacer(modifier = Modifier.height(SpaceMedium))
                }

                item {
                    StandardButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON),
                        text = if (salaryId.isNotEmpty()) stringResource(id = R.string.update_salary_entry)
                        else stringResource(id = R.string.create_salary_entry),
                        icon = if (salaryId.isNotEmpty()) Icons.Default.Edit else Icons.Default.Add,
                        onClick = {
                            if (salaryId.isNotEmpty()) {
                                addEditSalaryViewModel.onEvent(
                                    AddEditSalaryEvent.UpdateSalaryEntry(salaryId)
                                )
                            } else {
                                addEditSalaryViewModel.onEvent(AddEditSalaryEvent.AddSalaryEntry)
                            }
                        },
                    )
                }
            }
        }
    }
}