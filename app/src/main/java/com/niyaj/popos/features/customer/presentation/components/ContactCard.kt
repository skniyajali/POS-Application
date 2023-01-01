package com.niyaj.popos.features.customer.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.TextWithIcon

@Composable
fun ContactCard(
    modifier: Modifier = Modifier,
    phoneNo: String,
    contactName: String? = null,
    contactEmail: String? = null,
    doesSelected: Boolean = false,
    onSelectProduct: () -> Unit = {},
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onSelectProduct()
            },
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.onSurface,
        border = if(doesSelected)
            BorderStroke(1.dp, MaterialTheme.colors.primary)
        else null,
        elevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            contactName?.let { it ->
                TextWithIcon(
                    text = it,
                    icon = Icons.Default.Person,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(SpaceMini))
            }

            TextWithIcon(
                text = phoneNo,
                icon = Icons.Default.PhoneAndroid,
                fontWeight = FontWeight.SemiBold,
            )

            contactEmail?.let { email ->
                Spacer(modifier = Modifier.height(SpaceMini))
                TextWithIcon(
                    text = email,
                    icon = Icons.Default.Email,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}