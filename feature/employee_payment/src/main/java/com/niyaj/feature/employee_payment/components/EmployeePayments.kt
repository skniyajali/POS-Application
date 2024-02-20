package com.niyaj.feature.employee_payment.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Employee
import com.niyaj.model.Payment
import com.niyaj.ui.components.IconBox
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithIcon

/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmployeePayments(
    employee: Employee,
    employeeSalaries: List<Payment>,
    onSelectEmployee: (String) -> Unit,
    doesSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    isExpanded: Boolean = false,
    onExpandChanged: (String) -> Unit,
    onClickAddSalaryBtn: (String) -> Unit
) {
    Card(
        onClick = {
            onSelectEmployee(employee.employeeId)
        },
        modifier = Modifier
            .testTag(employee.employeeName.plus("Tag"))
            .fillMaxWidth()
            .padding(SpaceSmall),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = isExpanded,
            onExpandChanged = {
                onExpandChanged(employee.employeeId)
            },
            title = {
                TextWithIcon(
                    text = employee.employeeName,
                    icon = Icons.Default.Person,
                    isTitle = true
                )
            },
            trailing = {
                IconBox(
                    text = "Add Entry",
                    icon = Icons.Default.Add,
                    onClick = {
                        onClickAddSalaryBtn(employee.employeeId)
                    }
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onSelectEmployee(employee.employeeId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                EmployeePaymentsData(
                    employeeSalaries = employeeSalaries,
                    doesSelected = doesSelected,
                    onClick = onClick,
                    onLongClick = onLongClick
                )
            }
        )
    }
}
