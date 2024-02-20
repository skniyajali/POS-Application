package com.niyaj.feature.employee_attendance.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toDate
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Attendance
import com.niyaj.ui.components.TextWithBorderCount

/**
 *
 */
@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun EmployeeAbsentData(
    groupedAttendances: Map<String, List<Attendance>>,
    doesSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.height(SpaceMini))

        groupedAttendances.forEach { grouped ->
            TextWithBorderCount(
                modifier = Modifier,
                text = grouped.key,
                leadingIcon = Icons.Default.CalendarMonth,
                count = grouped.value.size,
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMini),
            ) {
                grouped.value.forEach { attendance ->
                    key(attendance.attendeeId) {
                        Card(
                            modifier = Modifier
                                .testTag(attendance.employee?.employeeName.plus(attendance.absentDate.toDate))
                                .combinedClickable(
                                    onClick = {
                                        onClick(attendance.attendeeId)
                                    },
                                    onLongClick = {
                                        onLongClick(attendance.attendeeId)
                                    },
                                ),
                            backgroundColor = LightColor6,
                            elevation = 2.dp,
                            border = if (doesSelected(attendance.attendeeId)) BorderStroke(
                                1.dp,
                                MaterialTheme.colors.primary
                            ) else null,
                        ) {
                            Text(
                                text = attendance.absentDate.toDate,
                                style = MaterialTheme.typography.body1,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colors.secondaryVariant,
                                modifier = Modifier
                                    .padding(SpaceSmall)
                            )
                        }

                        Spacer(modifier = Modifier.width(SpaceSmall))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(SpaceMini))
    }
}