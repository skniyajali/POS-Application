package com.niyaj.feature.employee_attendance.add_edit

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
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.AbsentScreenTestTags.ABSENT_DATE_FIELD
import com.niyaj.common.tags.AbsentScreenTestTags.ABSENT_EMPLOYEE_NAME_ERROR
import com.niyaj.common.tags.AbsentScreenTestTags.ABSENT_EMPLOYEE_NAME_FIELD
import com.niyaj.common.tags.AbsentScreenTestTags.ABSENT_REASON_FIELD
import com.niyaj.common.tags.AbsentScreenTestTags.ADD_EDIT_ABSENT_ENTRY_BTN
import com.niyaj.common.tags.AbsentScreenTestTags.CREATE_NEW_ABSENT
import com.niyaj.common.tags.AbsentScreenTestTags.EDIT_ABSENT_ITEM
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toSalaryDate
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.CircularBox
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
 * Add/Edit Absent Screen
 * @author Sk Niyaj Ali
 * @param attendanceId
 * @param employeeId
 * @param navController
 * @param resultBackNavigator
 * @param viewModel
 * @see AddEditAbsentViewModel
 */
@OptIn(ExperimentalMaterialApi::class)
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditAbsentScreen(
    attendanceId: String = "",
    employeeId: String = "",
    navController: NavController = rememberNavController(),
    viewModel: AddEditAbsentViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val lazyListState = rememberLazyListState()
    val employees = viewModel.employees.collectAsStateWithLifecycle().value

    val employeeError = viewModel.employeeError.collectAsStateWithLifecycle().value
    val dateError = viewModel.dateError.collectAsStateWithLifecycle().value

    val enableBtn = listOf(employeeError, dateError).all { it == null }

    val selectedEmployee = viewModel.selectedEmployee.collectAsStateWithLifecycle().value

    val title = if (attendanceId.isEmpty()) CREATE_NEW_ABSENT else EDIT_ABSENT_ITEM

    var employeeToggled by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val dialogState = rememberMaterialDialogState()

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
        modifier = Modifier.fillMaxWidth(),
        text = title,
        icon = Icons.Default.EventBusy,
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
            item(ABSENT_EMPLOYEE_NAME_FIELD) {
                ExposedDropdownMenuBox(
                    expanded = employeeToggled,
                    onExpandedChange = {
                        employeeToggled = !employeeToggled
                    },
                    modifier = Modifier.testTag(ABSENT_EMPLOYEE_NAME_FIELD)
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = selectedEmployee.employeeName,
                        label = ABSENT_EMPLOYEE_NAME_FIELD,
                        leadingIcon = Icons.Default.Person4,
                        error = employeeError,
                        errorTag = ABSENT_EMPLOYEE_NAME_ERROR,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = employeeToggled)
                        },
                    )

                    DropdownMenu(
                        expanded = employeeToggled,
                        onDismissRequest = {
                            employeeToggled = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                    ) {
                        employees.forEach { employee ->
                            CustomDropdownMenuItem(
                                modifier = Modifier
                                    .testTag(employee.employeeName),
                                text = {
                                    Text(
                                        text = employee.employeeName,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                leadingIcon = {
                                    CircularBox(
                                        icon = Icons.Default.Person,
                                        doesSelected = selectedEmployee.employeeName == employee.employeeName,
                                        text = employee.employeeName
                                    )
                                },
                                onClick = {
                                    viewModel.onEvent(AddEditAbsentEvent.OnSelectEmployee(employee))
                                    employeeToggled = false
                                }
                            )

                            Divider(modifier = Modifier.fillMaxWidth())
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

            item(ABSENT_DATE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(ABSENT_DATE_FIELD),
                    text = viewModel.state.absentDate.toSalaryDate,
                    label = ABSENT_DATE_FIELD,
                    leadingIcon = Icons.Default.CalendarToday,
                    error = dateError,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { dialogState.show() },
                            modifier = Modifier.testTag(ABSENT_DATE_FIELD)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            item(ABSENT_REASON_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(ABSENT_REASON_FIELD),
                    text = viewModel.state.absentReason,
                    label = ABSENT_REASON_FIELD,
                    leadingIcon = Icons.AutoMirrored.Filled.EventNote,
                    onValueChange = {
                        viewModel.onEvent(AddEditAbsentEvent.AbsentReasonChanged(it))
                    },
                )
            }

            item(ADD_EDIT_ABSENT_ENTRY_BTN) {
                Spacer(modifier = Modifier.height(SpaceMini))

                StandardButtonFW(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ADD_EDIT_ABSENT_ENTRY_BTN),
                    text = title,
                    enabled = enableBtn,
                    icon = if (attendanceId.isNotEmpty()) Icons.Default.Edit else Icons.Default.Add,
                    onClick = {
                        viewModel.onEvent(AddEditAbsentEvent.CreateOrUpdateAbsent)
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
            viewModel.onEvent(AddEditAbsentEvent.AbsentDateChanged(date.toMilliSecond))
        }
    }

}