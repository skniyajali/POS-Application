package com.niyaj.feature.employee.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Money
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.common.tags.EmployeeTestTags
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.model.PaymentStatus
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PaymentStatusChip
import com.niyaj.ui.components.SalaryDateDropdown
import com.niyaj.ui.event.UiState
import com.niyaj.ui.util.toUiStatus

/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SalaryEstimationCard(
    modifier: Modifier = Modifier,
    uiState: UiState<EmployeeSalaryEstimation>,
    dropdownText: String = "",
    salaryDates: List<EmployeeMonthlyDate> = emptyList(),
    onDateClick: (Pair<String, String>) -> Unit = {},
    onClickPaymentCount: () -> Unit = {},
    onClickAbsentCount: () -> Unit = {},
    onClickAbsentEntry: () -> Unit = {},
    onClickSalaryEntry: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .testTag("CalculateSalary")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Salary Estimation",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                SalaryDateDropdown(
                    text = dropdownText,
                    salaryDates = salaryDates,
                    onDateClick = {
                        onDateClick(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))

            Crossfade(
                targetState = uiState,
                label = "SalaryEstimationState"
            ) { state ->
                when (state) {
                    is UiState.Loading -> LoadingIndicator()

                    is UiState.Empty -> {
                        Text(
                            text = "Something went wrong!",
                            textAlign = TextAlign.Center,
                        )
                    }

                    is UiState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = state.data.remainingAmount.toRupee,
                                    style = MaterialTheme.typography.h5,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.testTag(EmployeeTestTags.REMAINING_AMOUNT_TEXT)
                                )

                                Column(
                                    horizontalAlignment = Alignment.End,
                                ) {
                                    PaymentStatusChip(
                                        paymentStatus = state.data.status.toUiStatus()
                                    )

                                    state.data.message?.let {
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        Text(
                                            text = it,
                                            color = if (state.data.status == PaymentStatus.Paid) {
                                                MaterialTheme.colors.error
                                            } else MaterialTheme.colors.primary,
                                            style = MaterialTheme.typography.caption,
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Card(
                                    onClick = onClickPaymentCount,
                                    backgroundColor = LightColor6,
                                    modifier = Modifier.testTag("AdvancePayment")
                                ) {
                                    Text(
                                        text = "${state.data.paymentCount} Advance Payment",
                                        style = MaterialTheme.typography.body2,
                                        modifier = Modifier.padding(SpaceSmall)
                                    )
                                }
                                Spacer(modifier = Modifier.width(SpaceSmall))
                                Card(
                                    onClick = onClickAbsentCount,
                                    backgroundColor = LightColor6,
                                    modifier = Modifier.testTag("DaysAbsent")
                                ) {
                                    Text(
                                        text = "${state.data.absentCount} Days Absent",
                                        style = MaterialTheme.typography.body2,
                                        modifier = Modifier.padding(SpaceSmall)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            Button(
                                onClick = onClickAbsentEntry,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(ButtonSize),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = LightColor6,
                                    contentColor = Color.Black
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventBusy,
                                    contentDescription = "Add Absent Entry Button",
                                    tint = MaterialTheme.colors.error
                                )
                                Spacer(modifier = Modifier.width(SpaceMini))
                                Text(
                                    text = "Add Absent Entry".uppercase(),
                                    style = MaterialTheme.typography.button,
                                    color = MaterialTheme.colors.error
                                )
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            Button(
                                onClick = onClickSalaryEntry,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(ButtonSize),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.secondaryVariant,
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Money,
                                    contentDescription = "Add Payment Entry Button"
                                )
                                Spacer(modifier = Modifier.width(SpaceMini))
                                Text(
                                    text = "Add Payment Entry".uppercase(),
                                    style = MaterialTheme.typography.button,
                                )
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }
        }
    }
}
