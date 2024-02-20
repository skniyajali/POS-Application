package com.niyaj.feature.employee.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.EmployeeTestTags
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Employee
import com.niyaj.ui.components.CircularBox

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun EmployeeData(
    modifier: Modifier = Modifier,
    item: Employee,
    doesSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.secondary),
) = trace("EmployeeData") {
    val borderStroke = if (doesSelected(item.employeeId)) border else null

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .testTag(EmployeeTestTags.EMPLOYEE_TAG.plus(item.employeeId))
            .shadow(2.dp, RoundedCornerShape(SpaceMini))
            .clip(RoundedCornerShape(SpaceMini))
            .background(MaterialTheme.colors.surface)
            .then(borderStroke?.let {
                Modifier.border(it, RoundedCornerShape(SpaceMini))
            } ?: Modifier)
            .clip(RoundedCornerShape(SpaceMini))
            .combinedClickable(
                onClick = {
                    onClick(item.employeeId)
                },
                onLongClick = {
                    onLongClick(item.employeeId)
                },
            ),
        text = {
            Text(
                text = item.employeeName,
                style = MaterialTheme.typography.body1
            )
        },
        secondaryText = {
            Text(text = item.employeePhone)
        },
        icon = {
            CircularBox(
                backgroundColor = MaterialTheme.colors.background,
                icon = Icons.Default.Person,
                doesSelected = doesSelected(item.employeeId),
                text = item.employeeName
            )
        },
        trailing = {
            Icon(
                Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = "Localized description",
            )
        },
    )
}