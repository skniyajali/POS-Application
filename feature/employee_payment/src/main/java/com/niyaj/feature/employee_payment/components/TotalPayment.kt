package com.niyaj.feature.employee_payment.components

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
import androidx.compose.material.icons.filled.People
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.TextWithIcon


/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TotalPayment(
    paymentsCount: Int = 0,
    employeesCount: Int = 0,
    onClickTotalPayments: () -> Unit,
    totalAmount: String = "0",
    onClickAbsentEntry: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Total Payments",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    onClick = onClickTotalPayments,
                    backgroundColor = LightColor6,
                    modifier = Modifier.testTag("TotalPayments")
                ) {
                    TextWithIcon(
                        modifier = Modifier
                            .padding(SpaceMini),
                        text = "$paymentsCount Payments",
                        icon = Icons.Default.Money
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = totalAmount.toRupee,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("TotalAmount")
                )

                TextWithIcon(
                    modifier = Modifier
                        .padding(SpaceMini),
                    text = "$employeesCount Employees",
                    icon = Icons.Default.People
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))
            Spacer(modifier = Modifier.height(SpaceSmall))

            Button(
                onClick = onClickAbsentEntry,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(ButtonSize),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondaryVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = "Add Absent Entry",
                )
                Spacer(modifier = Modifier.width(SpaceMini))
                Text(
                    text = "Add Absent Entry".uppercase(),
                    style = MaterialTheme.typography.button,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}