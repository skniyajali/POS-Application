package com.niyaj.popos.features.employee_attendance.presentation.add_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.EventNote
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.common.utils.toMilliSecond
import com.niyaj.popos.common.utils.toSalaryDate
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButtonFW
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentScreenTestTags.ABSENT_DATE_FIELD
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentScreenTestTags.ABSENT_EMPLOYEE_NAME_ERROR
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentScreenTestTags.ABSENT_EMPLOYEE_NAME_FIELD
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentScreenTestTags.ABSENT_REASON_FIELD
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentScreenTestTags.ADD_EDIT_ABSENT_ENTRY_BTN
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import io.sentry.compose.SentryTraced
import java.time.LocalDate


/**
 * Add/Edit Absent Screen
 * @author Sk Niyaj Ali
 * @param attendanceId
 * @param employeeId
 * @param navController
 * @param resultBackNavigator
 * @param absentViewModel
 * @see AbsentViewModel
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditAbsentScreen(
    attendanceId: String = "",
    employeeId: String = "",
    navController: NavController = rememberNavController(),
    resultBackNavigator: ResultBackNavigator<String>,
    absentViewModel: AbsentViewModel = hiltViewModel(),
) {

    val employees = absentViewModel.employeeState.collectAsStateWithLifecycle().value.employees

    var employeeToggled by remember {
        mutableStateOf(false)
    }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val dialogState = rememberMaterialDialogState()

    LaunchedEffect(key1 = true) {
        absentViewModel.eventFlow.collect { event ->
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

    SentryTraced(tag = "AddEditAbsentScreen-$employeeId") {
        BottomSheetWithCloseDialog(
            modifier = Modifier.fillMaxWidth(),
            text = if (attendanceId.isNotEmpty())
                stringResource(id = R.string.update_absent_entry)
            else
                stringResource(id = R.string.create_absent_entry),
            icon = Icons.Default.EventBusy,
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
                        if (absentViewModel.absentState.employee.employeeId.isNotEmpty()) {
                            (date.toMilliSecond >= absentViewModel.absentState.employee.employeeJoinedDate) && (date <= LocalDate.now())
                        } else date == LocalDate.now()
                    }
                ) {date ->
                    absentViewModel.onEvent(
                        AbsentEvent.AbsentDateChanged(date.toMilliSecond)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
            ) {
                ExposedDropdownMenuBox(
                    expanded = employees.isNotEmpty() && employeeToggled,
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
                        text = absentViewModel.absentState.employee.employeeName,
                        label = "Employee Name",
                        leadingIcon = Icons.Default.Person4,
                        error = absentViewModel.absentState.employeeError,
                        errorTag = ABSENT_EMPLOYEE_NAME_ERROR,
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
                                    absentViewModel.onEvent(
                                        AbsentEvent.EmployeeChanged(employee.employeeId)
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

                StandardOutlinedTextField(
                    text = absentViewModel.absentState.absentDate.toSalaryDate,
                    label = "Absent Date",
                    leadingIcon = Icons.Default.CalendarToday,
                    error = absentViewModel.absentState.absentDateError,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { dialogState.show() },
                            modifier = Modifier.testTag(ABSENT_DATE_FIELD)
                        ) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardOutlinedTextField(
                    modifier = Modifier.testTag(ABSENT_REASON_FIELD),
                    text = absentViewModel.absentState.absentReason,
                    label = "Absent Reason",
                    leadingIcon = Icons.Default.EventNote,
                    onValueChange = {
                        absentViewModel.onEvent(AbsentEvent.AbsentReasonChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))

                StandardButtonFW(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ADD_EDIT_ABSENT_ENTRY_BTN),
                    text = if (attendanceId.isNotEmpty()) stringResource(id = R.string.update_absent_entry)
                    else stringResource(id = R.string.create_absent_entry),
                    icon = if (attendanceId.isNotEmpty()) Icons.Default.Edit else Icons.Default.Add,
                    onClick = {
                        if (attendanceId.isNotEmpty()) {
                            absentViewModel.onEvent(
                                AbsentEvent.UpdateAbsentEntry(attendanceId)
                            )
                        } else {
                            absentViewModel.onEvent(AbsentEvent.AddAbsentEntry)
                        }
                    },
                )
            }
        }
    }

}