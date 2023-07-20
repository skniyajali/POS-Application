package com.niyaj.popos.features.employee.presentation.add_edit

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
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.Primary
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButtonFW
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.employee.domain.util.EmployeeSalaryType
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.ADD_EDIT_EMPLOYEE_BUTTON
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_JOINED_DATE_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_MONTHLY_SALARY_ERROR
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_MONTHLY_SALARY_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_NAME_ERROR
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_NAME_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_PHONE_ERROR
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_PHONE_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_POSITION_ERROR
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_POSITION_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_SALARY_TYPE_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_TYPE_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeType
import com.niyaj.popos.utils.toMilliSecond
import com.niyaj.popos.utils.toSalaryDate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

/**
 * Add/Edit Employee Screen
 * @author Sk Niyaj Ali
 * @param employeeId
 * @param navController
 * @param viewModel
 * @param resultBackNavigator
 * @see AddEditEmployeeViewModel
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Destination
@Composable
fun AddEditEmployeeScreen(
    employeeId : String = "",
    navController : NavController,
    viewModel : AddEditEmployeeViewModel = hiltViewModel(),
    resultBackNavigator : ResultBackNavigator<String>
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
            color = Primary, darkIcons = false
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
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

    SentryTraced(tag = "AddEditEmployeeScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            title = {
                Text(text = if (employeeId.isEmpty()) "Create New Employee" else "Update Employee")
            },
            showBackArrow = true,
        ) {
            MaterialDialog(dialogState = dialogState, buttons = {
                positiveButton("Ok")
                negativeButton("Cancel")
            }) {
                datepicker(allowedDateValidator = { date ->
                    date <= LocalDate.now()
                }) { date ->
                    viewModel.onEvent(AddEditEmployeeEvent.EmployeeJoinedDateChanged(date.toMilliSecond))
                }
            }


            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(SpaceSmall),
            ) {
                item {
                    StandardOutlinedTextField(
                        modifier = Modifier.testTag(EMPLOYEE_NAME_FIELD),
                        text = viewModel.addEditState.employeeName,
                        label = "Employee Name",
                        errorTag = EMPLOYEE_NAME_ERROR,
                        leadingIcon = Icons.Default.Person4,
                        error = viewModel.addEditState.employeeNameError,
                        onValueChange = {
                            viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged(it))
                        },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    StandardOutlinedTextField(
                        modifier = Modifier.testTag(EMPLOYEE_PHONE_FIELD),
                        text = viewModel.addEditState.employeePhone,
                        label = "Employee Phone",
                        leadingIcon = Icons.Default.PhoneAndroid,
                        keyboardType = KeyboardType.Number,
                        error = viewModel.addEditState.employeePhoneError,
                        errorTag = EMPLOYEE_PHONE_ERROR,
                        onValueChange = {
                            viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged(it))
                        },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(SpaceSmall))
                    StandardOutlinedTextField(
                        modifier = Modifier.testTag(EMPLOYEE_MONTHLY_SALARY_FIELD),
                        text = viewModel.addEditState.employeeSalary,
                        label = "Employee Monthly Salary",
                        leadingIcon = Icons.Default.Money,
                        keyboardType = KeyboardType.Number,
                        error = viewModel.addEditState.employeeSalaryError,
                        errorTag = EMPLOYEE_MONTHLY_SALARY_ERROR,
                        onValueChange = {
                            viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged(it))
                        },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    ExposedDropdownMenuBox(
                        expanded = salaryTypeToggled,
                        onExpandedChange = {
                            salaryTypeToggled = !salaryTypeToggled
                        },
                        modifier = Modifier.testTag(EMPLOYEE_SALARY_TYPE_FIELD),
                    ) {
                        StandardOutlinedTextField(
                            modifier = Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
                                    //This value is used to assign to the DropDown the same width
                                    textFieldSize = coordinates.size.toSize()
                                },
                            text = viewModel.addEditState.employeeSalaryType,
                            label = "Employee Salary Type",
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
                            modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                        ) {
                            DropdownMenuItem(modifier = Modifier.fillMaxWidth(), onClick = {
                                viewModel.onEvent(
                                    AddEditEmployeeEvent.EmployeeSalaryTypeChanged(
                                        EmployeeSalaryType.Monthly.salaryType
                                    )
                                )
                                salaryTypeToggled = false
                            }) {
                                Text(
                                    text = EmployeeSalaryType.Monthly.salaryType,
                                    style = MaterialTheme.typography.body1,
                                )
                            }

                            Divider(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Gray,
                                thickness = 0.8.dp
                            )

                            DropdownMenuItem(modifier = Modifier.fillMaxWidth(), onClick = {
                                viewModel.onEvent(
                                    AddEditEmployeeEvent.EmployeeSalaryTypeChanged(
                                        EmployeeSalaryType.Daily.salaryType
                                    )
                                )
                                salaryTypeToggled = false
                            }) {
                                Text(
                                    text = EmployeeSalaryType.Daily.salaryType,
                                    style = MaterialTheme.typography.body1,
                                )
                            }

                            Divider(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Gray,
                                thickness = 0.8.dp
                            )

                            DropdownMenuItem(modifier = Modifier.fillMaxWidth(), onClick = {
                                viewModel.onEvent(
                                    AddEditEmployeeEvent.EmployeeSalaryTypeChanged(
                                        EmployeeSalaryType.Weekly.salaryType
                                    )
                                )
                                salaryTypeToggled = false
                            }) {
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
                        expanded = viewModel.expanded,
                        onExpandedChange = {
                            viewModel.expanded = !viewModel.expanded
                        },
                        modifier = Modifier.testTag(EMPLOYEE_TYPE_FIELD),
                    ) {
                        StandardOutlinedTextField(
                            modifier = Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
                                    //This value is used to assign to the DropDown the same width
                                    textFieldSize = coordinates.size.toSize()
                                },
                            text = viewModel.addEditState.employeeType,
                            label = "Employee Type",
                            leadingIcon = Icons.Default.Accessibility,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = viewModel.expanded
                                )
                            },
                        )
                        DropdownMenu(
                            expanded = viewModel.expanded,
                            onDismissRequest = {
                                viewModel.expanded = false
                            },
                            modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                        ) {
                            DropdownMenuItem(modifier = Modifier.fillMaxWidth(), onClick = {
                                viewModel.onEvent(
                                    AddEditEmployeeEvent.EmployeeTypeChanged(
                                        EmployeeType.FullTime.employeeType
                                    )
                                )
                                viewModel.expanded = false
                            }) {
                                Text(
                                    text = EmployeeType.FullTime.employeeType,
                                    style = MaterialTheme.typography.body1,
                                )
                            }

                            Divider(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Gray,
                                thickness = 0.8.dp
                            )

                            DropdownMenuItem(modifier = Modifier.fillMaxWidth(), onClick = {
                                viewModel.onEvent(
                                    AddEditEmployeeEvent.EmployeeTypeChanged(
                                        EmployeeType.PartTime.employeeType
                                    )
                                )
                                viewModel.expanded = false
                            }) {
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

                    ExposedDropdownMenuBox(
                        expanded = positionDropdownToggled,
                        onExpandedChange = {
                            positionDropdownToggled = !positionDropdownToggled
                        },
                        modifier = Modifier.testTag(EMPLOYEE_POSITION_FIELD),
                    ) {
                        StandardOutlinedTextField(
                            modifier = Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
                                    //This value is used to assign to the DropDown the same width
                                    textFieldSize = coordinates.size.toSize()
                                },
                            text = viewModel.addEditState.employeePosition,
                            label = "Employee Position",
                            readOnly = true,
                            leadingIcon = Icons.Default.Star,
                            error = viewModel.addEditState.employeePositionError,
                            errorTag = EMPLOYEE_POSITION_ERROR,
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditEmployeeEvent.EmployeePositionChanged(
                                        it
                                    )
                                )
                                employeePositions.value =
                                    positions.filter { position -> position.contains(it, true) }
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
                            modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                        ) {
                            employeePositions.value.forEachIndexed { index, positionName ->
                                DropdownMenuItem(modifier = Modifier.fillMaxWidth(), onClick = {
                                    viewModel.onEvent(
                                        AddEditEmployeeEvent.EmployeePositionChanged(
                                            positionName
                                        )
                                    )
                                    positionDropdownToggled = false
                                }) {
                                    Text(
                                        text = positionName,
                                        style = MaterialTheme.typography.body1,
                                    )
                                }

                                if (index != employeePositions.value.size - 1) {
                                    Divider(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color.Gray,
                                        thickness = 0.8.dp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    StandardOutlinedTextField(text = viewModel.addEditState.employeeJoinedDate.toSalaryDate,
                        label = "Employee Joined Date",
                        leadingIcon = Icons.Default.CalendarMonth,
                        error = null,
                        onValueChange = {},
                        trailingIcon = {
                            IconButton(
                                onClick = { dialogState.show() },
                                modifier = Modifier.testTag(EMPLOYEE_JOINED_DATE_FIELD)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Choose Date"
                                )
                            }
                        })
                }

                item {
                    Spacer(modifier = Modifier.height(SpaceMedium))

                    StandardButtonFW(
                        modifier = Modifier.fillMaxWidth().testTag(ADD_EDIT_EMPLOYEE_BUTTON),
                        text = if (employeeId.isNotEmpty()) stringResource(id = R.string.update_employee)
                        else stringResource(id = R.string.create_new_employee),
                        icon = if (employeeId.isNotEmpty()) Icons.Default.Edit else Icons.Default.Add,
                        onClick = {
                            if (employeeId.isNotEmpty()) {
                                viewModel.onEvent(
                                    AddEditEmployeeEvent.UpdateEmployee(
                                        employeeId
                                    )
                                )
                            } else {
                                viewModel.onEvent(AddEditEmployeeEvent.CreateNewEmployee)
                            }
                        },
                    )
                }
            }
        }
    }
}