package com.niyaj.popos.features.employee_attendance.presentation.add_edit

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.util.localDateToCurrentMillis
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
fun AddEditAbsentScreen(
    attendanceId: String = "",
    employeeId: String = "",
    navController: NavController = rememberNavController(),
    resultBackNavigator: ResultBackNavigator<String>,
    absentViewModel: AbsentViewModel = hiltViewModel(),
) {

    val employees by lazy { absentViewModel.employeeState.employees }

    var employeeToggled by remember {
        mutableStateOf(false)
    }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val dialogState = rememberMaterialDialogState()

    LaunchedEffect(key1 = true) {
        absentViewModel.eventFlow.collect { event ->
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
        text = if (attendanceId.isNotEmpty())
            stringResource(id = R.string.update_absent_entry)
        else
            stringResource(id = R.string.create_absent_entry),
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
                    date <= LocalDate.now() && if (absentViewModel.absentState.employee.employeeId.isNotEmpty()) {
                        date.toMilliSecond >= absentViewModel.absentState.employee.employeeJoinedDate
                    } else true
                }
            ) {date ->
                absentViewModel.onEvent(
                    AbsentEvent.AbsentDateChanged(localDateToCurrentMillis(date))
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
                }
            ) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            //This is used to assign to the DropDown the same width
                            textFieldSize = coordinates.size.toSize()
                        },
                    text = absentViewModel.absentState.employee.employeeName,
                    hint = "Employee Name",
                    error = absentViewModel.absentState.employeeError,
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
                hint = "Absent Date",
                error = absentViewModel.absentState.absentDateError,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { dialogState.show() }) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                text = absentViewModel.absentState.absentReason,
                hint = "Absent Reason",
                onValueChange = {
                    absentViewModel.onEvent(AbsentEvent.AbsentReasonChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            Button(
                onClick = {
                    if (attendanceId.isNotEmpty()) {
                        absentViewModel.onEvent(
                            AbsentEvent.UpdateAbsentEntry(attendanceId)
                        )
                    } else {
                        absentViewModel.onEvent(AbsentEvent.AddAbsentEntry)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(ButtonSize),
            ) {
                Text(
                    text = if (attendanceId.isNotEmpty())
                        stringResource(id = R.string.update_absent_entry).uppercase()
                    else
                        stringResource(id = R.string.create_absent_entry).uppercase(),
                    style = MaterialTheme.typography.button,
                )
            }
        }

    }
}