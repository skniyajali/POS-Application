package com.niyaj.popos.features.employee.presentation.add_edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.Primary
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButton
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.employee.domain.util.EmployeeSalaryType
import com.niyaj.popos.features.employee.domain.util.EmployeeType
import com.niyaj.popos.util.toMilliSecond
import com.niyaj.popos.util.toSalaryDate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun AddEditEmployeeScreen(
    employeeId: String = "",
    navController: NavController,
    addEditEmployeeViewModel: AddEditEmployeeViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val scaffoldState = rememberScaffoldState()
    val systemUiController = rememberSystemUiController()
    val dialogState = rememberMaterialDialogState()

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    var positionDropdownToggled by remember {
        mutableStateOf(false)
    }

    var salaryTypeToggled by remember {
        mutableStateOf(false)
    }

    val employeePositions = remember {
        mutableStateOf(positions)
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Primary,
            darkIcons = false
        )
    }

    LaunchedEffect(key1 = true) {
        addEditEmployeeViewModel.eventFlow.collectLatest { event ->
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

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        title = {
            Text(text = if (employeeId.isEmpty()) "Create New Employee" else "Update Employee")
        },
        showBackArrow = true,
    ){

        MaterialDialog(
            dialogState = dialogState,
            buttons = {
                positiveButton("Ok")
                negativeButton("Cancel")
            }
        ) {
            datepicker(
                allowedDateValidator = { date ->
                    date <= LocalDate.now()
                }
            ) { date ->
                addEditEmployeeViewModel.onAddEditEmployeeEvent(
                    AddEditEmployeeEvent.EmployeeJoinedDateChanged(
                        date.toMilliSecond
                    )
                )
            }
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
        ){
            item {
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = addEditEmployeeViewModel.addEditState.employeeName,
                    hint = "Employee Name",
                    leadingIcon = Icons.Default.Person4,
                    error = addEditEmployeeViewModel.addEditState.employeeNameError,
                    onValueChange = {
                        addEditEmployeeViewModel.onAddEditEmployeeEvent(
                            AddEditEmployeeEvent.EmployeeNameChanged(
                                it
                            )
                        )
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = addEditEmployeeViewModel.addEditState.employeePhone,
                    hint = "Employee Phone",
                    leadingIcon = Icons.Default.PhoneAndroid,
                    keyboardType = KeyboardType.Number,
                    error = addEditEmployeeViewModel.addEditState.employeePhoneError,
                    onValueChange = {
                        addEditEmployeeViewModel.onAddEditEmployeeEvent(
                            AddEditEmployeeEvent.EmployeePhoneChanged(
                                it
                            )
                        )
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = addEditEmployeeViewModel.addEditState.employeeSalary,
                    hint = "Employee Monthly Salary",
                    leadingIcon = Icons.Default.Money,
                    keyboardType = KeyboardType.Number,
                    error = addEditEmployeeViewModel.addEditState.employeeSalaryError,
                    onValueChange = {
                        addEditEmployeeViewModel.onAddEditEmployeeEvent(
                            AddEditEmployeeEvent.EmployeeSalaryChanged(
                                it
                            )
                        )
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                ExposedDropdownMenuBox(
                    expanded = salaryTypeToggled,
                    onExpandedChange = {
                        salaryTypeToggled = !salaryTypeToggled
                    }
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = addEditEmployeeViewModel.addEditState.employeeSalaryType,
                        hint = "Employee Salary Type",
                        leadingIcon = Icons.Default.MergeType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = salaryTypeToggled
                            )
                        },
                    )

                    DropdownMenu(
                        expanded = salaryTypeToggled,
                        onDismissRequest = {
                            salaryTypeToggled = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                    ) {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                addEditEmployeeViewModel.onAddEditEmployeeEvent(
                                    AddEditEmployeeEvent.EmployeeSalaryTypeChanged(
                                        EmployeeSalaryType.Monthly.salaryType
                                    )
                                )
                                salaryTypeToggled = false
                            }
                        ) {
                            Text(
                                text = EmployeeSalaryType.Monthly.salaryType,
                                style = MaterialTheme.typography.body1,
                            )
                        }

                        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)

                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                addEditEmployeeViewModel.onAddEditEmployeeEvent(
                                    AddEditEmployeeEvent.EmployeeSalaryTypeChanged(
                                        EmployeeSalaryType.Daily.salaryType
                                    )
                                )
                                salaryTypeToggled = false
                            }
                        ) {
                            Text(
                                text = EmployeeSalaryType.Daily.salaryType,
                                style = MaterialTheme.typography.body1,
                            )
                        }

                        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)

                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                addEditEmployeeViewModel.onAddEditEmployeeEvent(
                                    AddEditEmployeeEvent.EmployeeSalaryTypeChanged(
                                        EmployeeSalaryType.Weekly.salaryType
                                    )
                                )
                                salaryTypeToggled = false
                            }
                        ) {
                            Text(
                                text = EmployeeSalaryType.Weekly.salaryType,
                                style = MaterialTheme.typography.body1,
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                ExposedDropdownMenuBox(
                    expanded = addEditEmployeeViewModel.expanded,
                    onExpandedChange = {
                        addEditEmployeeViewModel.expanded = !addEditEmployeeViewModel.expanded
                    }
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = addEditEmployeeViewModel.addEditState.employeeType,
                        hint = "Employee Type",
                        leadingIcon = Icons.Default.Accessibility,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = addEditEmployeeViewModel.expanded
                            )
                        },
                    )
                    DropdownMenu(
                        expanded = addEditEmployeeViewModel.expanded,
                        onDismissRequest = {
                            addEditEmployeeViewModel.expanded = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                    ) {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                addEditEmployeeViewModel.onAddEditEmployeeEvent(
                                    AddEditEmployeeEvent.EmployeeTypeChanged(
                                        EmployeeType.FullTime.employeeType
                                    )
                                )
                                addEditEmployeeViewModel.expanded = false
                            }
                        ) {
                            Text(
                                text = EmployeeType.FullTime.employeeType,
                                style = MaterialTheme.typography.body1,
                            )
                        }

                        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)

                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                addEditEmployeeViewModel.onAddEditEmployeeEvent(
                                    AddEditEmployeeEvent.EmployeeTypeChanged(
                                        EmployeeType.PartTime.employeeType
                                    )
                                )
                                addEditEmployeeViewModel.expanded = false
                            }
                        ) {
                            Text(
                                text = EmployeeType.PartTime.employeeType,
                                style = MaterialTheme.typography.body1,
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = addEditEmployeeViewModel.addEditState.employeePosition,
                        hint = "Employee Position",
                        leadingIcon = Icons.Default.Star,
                        error = addEditEmployeeViewModel.addEditState.employeePositionError,
                        onValueChange = {
                            addEditEmployeeViewModel.onAddEditEmployeeEvent(
                                AddEditEmployeeEvent.EmployeePositionChanged(
                                    it
                                )
                            )
                            employeePositions.value = positions.filter { position ->  position.contains(it, true) }
                            positionDropdownToggled = true //
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = positionDropdownToggled
                            )
                        },
                    )
                    DropdownMenu(
                        expanded = employeePositions.value.isNotEmpty() && positionDropdownToggled,
                        properties = PopupProperties(
                            focusable = false,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                        ),
                        onDismissRequest = {
                            positionDropdownToggled = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                    ) {
                        employeePositions.value.forEachIndexed{ index, positionName ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    addEditEmployeeViewModel.onAddEditEmployeeEvent(
                                        AddEditEmployeeEvent.EmployeePositionChanged(positionName)
                                    )
                                    positionDropdownToggled = false
                                }
                            ) {
                                Text(
                                    text = positionName,
                                    style = MaterialTheme.typography.body1,
                                )
                            }

                            if(index != employeePositions.value.size - 1) {
                                Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardOutlinedTextField(
                    text = addEditEmployeeViewModel.addEditState.employeeJoinedDate.toSalaryDate,
                    hint = "Employee Joined Date",
                    leadingIcon = Icons.Default.CalendarMonth,
                    error = null,
                    onValueChange = {},
                    trailingIcon = {
                        IconButton(onClick = { dialogState.show() }) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardButton(
                    text = if (employeeId.isNotEmpty()) stringResource(id = R.string.update_employee)
                        else stringResource(id = R.string.create_new_employee),
                    icon = if (employeeId.isNotEmpty()) Icons.Default.Edit else Icons.Default.Add,
                    onClick = {
                        if (employeeId.isNotEmpty()) {
                            addEditEmployeeViewModel.onAddEditEmployeeEvent(
                                AddEditEmployeeEvent.UpdateEmployee(
                                    employeeId
                                )
                            )
                        } else {
                            addEditEmployeeViewModel.onAddEditEmployeeEvent(AddEditEmployeeEvent.CreateNewEmployee)
                        }
                    },
                )
            }
        }
    }
}