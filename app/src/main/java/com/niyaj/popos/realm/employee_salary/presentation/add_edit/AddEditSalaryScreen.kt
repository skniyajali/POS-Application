package com.niyaj.popos.realm.employee_salary.presentation.add_edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
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
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.StandardOutlinedTextField
import com.niyaj.popos.presentation.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.presentation.ui.theme.ButtonSize
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.realm.employee.domain.util.PaymentType
import com.niyaj.popos.realm.employee.domain.util.SalaryType
import com.niyaj.popos.util.toMilliSecond
import com.niyaj.popos.util.toSalaryDate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditSalaryScreen(
    salaryId: String = "",
    employeeId: String = "",
    navController: NavController = rememberNavController(),
    addEditSalaryViewModel: AddEditSalaryViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    val employees by lazy { addEditSalaryViewModel.employeeState.employees }

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

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = if (salaryId.isNotEmpty())
            stringResource(id = R.string.update_salary_entry)
        else
            stringResource(id = R.string.create_salary_entry),
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
                    date <= LocalDate.now() && if (addEditSalaryViewModel.addEditSalaryState.employee.employeeId.isNotEmpty()) {
                        date.toMilliSecond >= addEditSalaryViewModel.addEditSalaryState.employee.employeeJoinedDate
                    } else true
                }
            ) { date ->
                addEditSalaryViewModel.onEvent(
                    AddEditSalaryEvent.SalaryDateChanged(date.toMilliSecond)
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
                    }
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
                        error = addEditSalaryViewModel.addEditSalaryState.employeeError,
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
                                modifier = Modifier.fillMaxWidth(),
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
                    }
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
                        hint = "Salary Type",
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
                            modifier = Modifier.fillMaxWidth(),
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
                            modifier = Modifier.fillMaxWidth(),
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
                    text = addEditSalaryViewModel.addEditSalaryState.salaryDate.toSalaryDate,
                    hint = "Given Date",
                    error = addEditSalaryViewModel.addEditSalaryState.salaryDateError,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { dialogState.show() }) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
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
                    }
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
                        hint = "Payment Type",
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
                            modifier = Modifier.fillMaxWidth(),
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
                            modifier = Modifier.fillMaxWidth(),
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
                            modifier = Modifier.fillMaxWidth(),
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
                    text = addEditSalaryViewModel.addEditSalaryState.salary,
                    hint = "Given Amount",
                    error = addEditSalaryViewModel.addEditSalaryState.salaryError,
                    keyboardType = KeyboardType.Number,
                    onValueChange = {
                        addEditSalaryViewModel.onEvent(AddEditSalaryEvent.SalaryChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                StandardOutlinedTextField(
                    text = addEditSalaryViewModel.addEditSalaryState.salaryNote,
                    hint = "Salary Note",
                    error = addEditSalaryViewModel.addEditSalaryState.salaryNoteError,
                    onValueChange = {
                        addEditSalaryViewModel.onEvent(AddEditSalaryEvent.SalaryNoteChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            item {
                Button(
                    onClick = {
                        if (salaryId.isNotEmpty()) {
                            addEditSalaryViewModel.onEvent(
                                AddEditSalaryEvent.UpdateSalaryEntry(salaryId)
                            )
                        } else {
                            addEditSalaryViewModel.onEvent(AddEditSalaryEvent.AddSalaryEntry)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(ButtonSize),
                ) {
                    Text(
                        text = if (salaryId.isNotEmpty())
                            stringResource(id = R.string.update_salary_entry).uppercase()
                        else
                            stringResource(id = R.string.create_salary_entry).uppercase(),
                        style = MaterialTheme.typography.button,
                    )
                }
            }

        }
    }
}