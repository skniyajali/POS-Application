package com.niyaj.popos.features.reports.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.TextWithIcon

@Composable
fun CustomerReportCard(
    modifier: Modifier = Modifier,
    customerPhoneNo: String,
    customerName: String? = null,
    customerEmail: String? = null,
    orderQty: Int = 0,
) {
    Row (
        modifier = modifier
            .padding(SpaceSmall)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            customerName?.let { it ->
                TextWithIcon(
                    text = it,
                    icon = Icons.Default.Person,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(SpaceMini))
            }

            TextWithIcon(
                text = customerPhoneNo,
                icon = Icons.Default.PhoneAndroid,
                fontWeight = FontWeight.SemiBold,
            )

            customerEmail?.let { email ->
                Spacer(modifier = Modifier.height(SpaceMini))
                TextWithIcon(
                    text = email,
                    icon = Icons.Default.Email,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(modifier = Modifier.height(SpaceMini))

        Text(
            text = orderQty.toString(),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.secondaryVariant,
        )
    }
}