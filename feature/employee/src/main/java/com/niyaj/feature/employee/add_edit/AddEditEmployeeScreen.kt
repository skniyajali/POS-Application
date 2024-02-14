package com.niyaj.feature.employee.add_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberScaffoldState
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
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.EmployeeTestTags.ADD_EDIT_EMPLOYEE_BUTTON
import com.niyaj.common.tags.EmployeeTestTags.CREATE_NEW_EMPLOYEE
import com.niyaj.common.tags.EmployeeTestTags.EDIT_EMPLOYEE
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_JOINED_DATE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_MONTHLY_SALARY_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_MONTHLY_SALARY_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_POSITION_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_POSITION_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_TYPE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_TYPE_FIELD
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toSalaryDate
import com.niyaj.designsystem.theme.IconSizeExtraLarge
import com.niyaj.designsystem.theme.LightColor7
import com.niyaj.designsystem.theme.ProfilePictureSizeLarge
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType
import com.niyaj.ui.components.PhoneNoCountBox
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
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
@OptIn(ExperimentalMaterialApi::class)
@Destination(route = Screens.ADD_EDIT_EMPLOYEE_SCREEN)
@Composable
fun AddEditEmployeeScreen(
    employeeId: String = "",
    navController: NavController,
    viewModel: AddEditEmployeeViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val scaffoldState = rememberScaffoldState()
    val dialogState = rememberMaterialDialogState()

    val phoneError = viewModel.phoneError.collectAsStateWithLifecycle().value
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val salaryError = viewModel.salaryError.collectAsStateWithLifecycle().value
    val positionError = viewModel.positionError.collectAsStateWithLifecycle().value

    val enableBtn = listOf(phoneError, nameError, salaryError, positionError).all {
        it == null
    }

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }

                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    val title = if (employeeId.isEmpty()) CREATE_NEW_EMPLOYEE else EDIT_EMPLOYEE

    var expanded by remember { mutableStateOf(false) }

    var salaryTypeToggled by remember { mutableStateOf(false) }

    var positionDropdownToggled by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    StandardScaffoldNew(
        navController = navController,
        scaffoldState = scaffoldState,
        title = title,
        showBackButton = true,
        showBottomBar = true,
        selectionCount = 0,
        showFab = false,
        floatingActionButton = {},
        navActions = {},
        bottomBar = {
            StandardButtonFW(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMedium)
                    .testTag(ADD_EDIT_EMPLOYEE_BUTTON),
                enabled = enableBtn,
                text = title,
                icon = if (employeeId.isNotEmpty()) Icons.Default.Edit else Icons.Default.Add,
                onClick = {
                    viewModel.onEvent(AddEditEmployeeEvent.CreateOrUpdateEmployee)
                },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item("Person icon") {
                Box(
                    modifier = Modifier
                        .size(ProfilePictureSizeLarge)
                        .background(LightColor7, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Person icon",
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .size(IconSizeExtraLarge)
                            .align(Alignment.Center)
                    )
                }
            }

            item(EMPLOYEE_NAME_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(EMPLOYEE_NAME_FIELD),
                    text = viewModel.state.employeeName,
                    label = "Employee Name",
                    errorTag = EMPLOYEE_NAME_ERROR,
                    leadingIcon = Icons.Default.Person4,
                    error = nameError,
                    onValueChange = {
                        viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged(it))
                    },
                )
            }

            item(EMPLOYEE_PHONE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(EMPLOYEE_PHONE_FIELD),
                    text = viewModel.state.employeePhone,
                    label = "Employee Phone",
                    leadingIcon = Icons.Default.PhoneAndroid,
                    keyboardType = KeyboardType.Number,
                    error = phoneError,
                    errorTag = EMPLOYEE_PHONE_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged(it))
                    },
                    trailingIcon = {
                        PhoneNoCountBox(
                            count = viewModel.state.employeePhone.length
                        )
                    },
                )
            }

            item(EMPLOYEE_MONTHLY_SALARY_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(EMPLOYEE_MONTHLY_SALARY_FIELD),
                    text = viewModel.state.employeeSalary,
                    label = "Employee Monthly Salary",
                    leadingIcon = Icons.Default.Money,
                    keyboardType = KeyboardType.Number,
                    error = salaryError,
                    errorTag = EMPLOYEE_MONTHLY_SALARY_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged(it))
                    },
                )
            }

            item(EMPLOYEE_SALARY_TYPE_FIELD) {
                ExposedDropdownMenuBox(
                    expanded = salaryTypeToggled,
                    onExpandedChange = {
                        salaryTypeToggled = !salaryTypeToggled
                    },
                    modifier = Modifier.testTag(EMPLOYEE_SALARY_TYPE_FIELD),
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = viewModel.state.employeeSalaryType.name,
                        label = "Employee Salary Type",
                        leadingIcon = Icons.AutoMirrored.Filled.MergeType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = salaryTypeToggled)
                        },
                    )

                    DropdownMenu(
                        expanded = salaryTypeToggled,
                        onDismissRequest = {
                            salaryTypeToggled = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current) {
                                textFieldSize.width.toDp()
                            }),
                    ) {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.onEvent(
                                    AddEditEmployeeEvent.EmployeeSalaryTypeChanged(
                                        EmployeeSalaryType.Monthly
                                    )
                                )
                                salaryTypeToggled = false
                            }
                        ) {
                            Text(
                                text = EmployeeSalaryType.Monthly.name,
                                style = MaterialTheme.typography.body1,
                            )
                        }

                        Divider(modifier = Modifier.fillMaxWidth())

                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.onEvent(
                                    AddEditEmployeeEvent.EmployeeSalaryTypeChanged(
                                        EmployeeSalaryType.Daily
                                    )
                                )
                                salaryTypeToggled = false
                            }
                        ) {
                            Text(
                                text = EmployeeSalaryType.Daily.name,
                                style = MaterialTheme.typography.body1,
                            )
                        }

                        Divider(modifier = Modifier.fillMaxWidth())

                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.onEvent(
                                    AddEditEmployeeEvent.EmployeeSalaryTypeChanged(
                                        EmployeeSalaryType.Weekly
                                    )
                                )
                                salaryTypeToggled = false
                            }
                        ) {
                            Text(
                                text = EmployeeSalaryType.Weekly.name,
                                style = MaterialTheme.typography.body1,
                            )
                        }
                    }
                }
            }

            item(EMPLOYEE_TYPE_FIELD) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    },
                    modifier = Modifier.testTag(EMPLOYEE_TYPE_FIELD),
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = viewModel.state.employeeType.name,
                        label = "Employee Type",
                        leadingIcon = Icons.Default.Accessibility,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        },
                        modifier = Modifier.width(with(LocalDensity.current) {
                            textFieldSize.width.toDp()
                        }),
                    ) {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.onEvent(
                                    AddEditEmployeeEvent.EmployeeTypeChanged(EmployeeType.FullTime)
                                )
                                expanded = false
                            }
                        ) {
                            Text(
                                text = EmployeeType.FullTime.name,
                                style = MaterialTheme.typography.body1,
                            )
                        }

                        Divider(modifier = Modifier.fillMaxWidth())

                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.onEvent(
                                    AddEditEmployeeEvent.EmployeeTypeChanged(EmployeeType.PartTime)
                                )
                                expanded = false
                            }
                        ) {
                            Text(
                                text = EmployeeType.PartTime.name,
                                style = MaterialTheme.typography.body1,
                            )
                        }
                    }
                }
            }

            item(EMPLOYEE_POSITION_FIELD) {
                ExposedDropdownMenuBox(
                    expanded = positionDropdownToggled,
                    onExpandedChange = {
                        positionDropdownToggled = !positionDropdownToggled
                    },
                    modifier = Modifier.testTag(EMPLOYEE_POSITION_FIELD),
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = viewModel.state.employeePosition,
                        label = "Employee Position",
                        readOnly = true,
                        leadingIcon = Icons.Default.Star,
                        error = positionError,
                        errorTag = EMPLOYEE_POSITION_ERROR,
                        onValueChange = {
                            viewModel.onEvent(AddEditEmployeeEvent.EmployeePositionChanged(it))
                            positionDropdownToggled = true //
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = positionDropdownToggled)
                        },
                    )
                    DropdownMenu(
                        expanded = positions.isNotEmpty() && positionDropdownToggled,
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
                        positions.forEachIndexed { index, positionName ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    viewModel.onEvent(
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

                            if (index != positions.size - 1) {
                                Divider(modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }

            item(EMPLOYEE_JOINED_DATE_FIELD) {
                StandardOutlinedTextField(
                    text = viewModel.state.employeeJoinedDate.toSalaryDate,
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
                    }
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
        datepicker(allowedDateValidator = { date ->
            date <= LocalDate.now()
        }) { date ->
            viewModel.onEvent(AddEditEmployeeEvent.EmployeeJoinedDateChanged(date.toMilliSecond))
        }
    }

}