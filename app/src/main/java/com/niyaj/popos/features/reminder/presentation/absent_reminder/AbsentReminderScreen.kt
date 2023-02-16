package com.niyaj.popos.features.reminder.presentation.absent_reminder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.IconSizeMedium
import com.niyaj.popos.features.common.ui.theme.LightColor16
import com.niyaj.popos.features.common.ui.theme.LightColor6
import com.niyaj.popos.features.common.ui.theme.LightColor9
import com.niyaj.popos.features.common.ui.theme.Pewter
import com.niyaj.popos.features.common.ui.theme.PoposPink300
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.Teal200
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.components.TextWithTitle
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.util.Constants.ABSENT_REMINDER_NOTE
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.delay

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AbsentReminderScreen(
    navController: NavController = rememberNavController(),
    absentReminderViewModel : AbsentReminderViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val employees = absentReminderViewModel.state.collectAsStateWithLifecycle().value.employees
    val isLoading = absentReminderViewModel.state.collectAsStateWithLifecycle().value.isLoading
    val hasError = absentReminderViewModel.state.collectAsStateWithLifecycle().value.error

    val selectedEmployees = absentReminderViewModel.selectedEmployees

    LaunchedEffect(key1 = employees) {
        if (employees.isEmpty()) {
            delay(300)
            navController.navigateUp()
        }
    }

    LaunchedEffect(key1 = true) {
        absentReminderViewModel.eventFlow.collect { event ->
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
        modifier = Modifier.fillMaxSize(),
        text = "Mark Absent Employee",
        icon = Icons.Default.EventBusy,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth()
        ) {
            if (hasError != null) {
                ItemNotAvailable(
                    text = hasError
                )
            }else {
                Column(
                    modifier = Modifier.weight(2.2F)
                ) {
                    EmployeeSelectionHeader(
                        text = "Employees",
                        icon = Icons.Default.People,
                        selectionCount = selectedEmployees.size,
                        checked = selectedEmployees.isNotEmpty(),
                        isLoading = isLoading,
                        onCheckedChange = {
                            absentReminderViewModel.onEvent(AbsentReminderEvent.SelectAllEmployee)
                        },
                    )
                    EmployeeSelectionBody(
                        employees = employees,
                        selectedEmployees = selectedEmployees,
                        isLoading = isLoading,
                        onCheckedChange = {
                            absentReminderViewModel.onEvent(AbsentReminderEvent.SelectEmployee(it))
                        },
                    )
                }

                Spacer(modifier = Modifier.height(SpaceMedium))

                Column(
                    modifier = Modifier.weight(0.8F)
                ) {

                    InfoCard(
                        isLoading = isLoading
                    )

                    EmployeeSelectionFooter(
                        primaryText = "Mark As Absent",
                        onPrimaryClick = {
                            absentReminderViewModel.onEvent(AbsentReminderEvent.MarkAbsent)
                        },
                        onSecondaryClick = {
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    modifier : Modifier = Modifier,
    isLoading : Boolean = false,
    backgroundColor : Color = Pewter,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        backgroundColor = backgroundColor,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info Icon",
                tint = Teal200,
                modifier = Modifier
                    .size(IconSizeMedium)
                    .placeholder(
                        visible = isLoading,
                        highlight = PlaceholderHighlight.shimmer(),
                        color = LightColor9
                    )
            )

            Spacer(modifier = Modifier.width(SpaceMini))

            Text(
                text = ABSENT_REMINDER_NOTE,
                style = MaterialTheme.typography.body1,
                maxLines = 2,
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = LightColor16,
                ),
            )
        }
    }
}

@Composable
fun EmployeeSelectionHeader(
    modifier: Modifier = Modifier,
    text: String,
    selectionCount: Int = 0,
    icon : ImageVector? = null,
    checked: Boolean = false,
    onCheckedChange: (String) -> Unit,
    isLoading : Boolean = false,
    backgroundColor : Color = PoposPink300,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = backgroundColor,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMini),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = LightColor9,
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        onCheckedChange(it.toString())
                    }
                )

                Text(
                    text = if (selectionCount != 0) "$selectionCount Selected" else "Select All",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.body1,
                )
            }

            TextWithTitle(
                text = text,
                icon = icon,
            )
        }
    }
}


@Composable
fun EmployeeSelectionBody(
    modifier: Modifier = Modifier,
    employees: List<Employee> = emptyList(),
    selectedEmployees: List<String> = emptyList(),
    onCheckedChange : (String) -> Unit,
    isLoading : Boolean = false,
    backgroundColor : Color = LightColor6,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = backgroundColor,
    ) {
        LazyColumn {
            itemsIndexed(employees) { index, employee ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceMini)
                        .clickable {
                            onCheckedChange(employee.employeeId)
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedEmployees.contains(employee.employeeId),
                            onCheckedChange = {
                                onCheckedChange(employee.employeeId)
                            },
                            modifier = Modifier.placeholder(
                                visible = isLoading,
                                highlight = PlaceholderHighlight.shimmer(),
                                color = LightColor9,
                            ),
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = MaterialTheme.colors.secondaryVariant,
                            )
                        )

                        TextWithIcon(
                            modifier = Modifier.placeholder(
                                visible = isLoading,
                                highlight = PlaceholderHighlight.shimmer(),
                                color = LightColor16,
                            ),
                            text = employee.employeeName,
                            isTitle = true,
                            icon = Icons.Default.Person4
                        )
                    }

                    TextWithTitle(
                        modifier = Modifier.placeholder(
                            visible = isLoading,
                            highlight = PlaceholderHighlight.shimmer(),
                            color = LightColor16,
                        ),
                        text = employee.employeePhone,
                        icon = Icons.Default.PhoneAndroid
                    )
                }

                if (index != employees.size -1) {
                    Divider(modifier = Modifier.fillMaxWidth())
//                    Spacer(modifier = Modifier.height(SpaceMini))
//                    Spacer(modifier = Modifier.height(SpaceMini))
                }
            }
        }
    }
}


@Composable
fun EmployeeSelectionFooter(
    modifier : Modifier = Modifier,
    primaryText: String,
    primaryIcon: ImageVector? = Icons.Default.EventAvailable,
    secondaryText: String = "Cancel, Do It Later",
    secondaryIcon: ImageVector? = Icons.Default.HighlightOff,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(SpaceMedium))

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedButton(
            onClick = onSecondaryClick,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(ButtonSize),
            border = BorderStroke(1.dp, MaterialTheme.colors.error),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colors.error
            ),
            shape = RoundedCornerShape(SpaceMini),
        ) {
            secondaryIcon?.let {
                Icon(
                    imageVector = secondaryIcon,
                    contentDescription = secondaryText,
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
            }

            Text(text = secondaryText.uppercase())
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        Button(
            onClick = onPrimaryClick,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(ButtonSize),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondaryVariant
            ),
            shape = RoundedCornerShape(SpaceMini),
        ) {
            primaryIcon?.let {
                Icon(
                    imageVector = primaryIcon,
                    contentDescription = primaryText,
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
            }

            Text(
                primaryText.uppercase(),
                style = MaterialTheme.typography.button
            )
        }
    }

    Spacer(modifier = Modifier.height(SpaceSmall))

}