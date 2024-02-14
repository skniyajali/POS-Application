package com.niyaj.feature.order.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Update
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Customer
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithIcon

/**
 * This composable displays the customer details
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomerDetails(
    customer: Customer,
    doesExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onClickViewDetails: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
    ) {
        StandardExpandable(
            onExpandChanged = {
                onExpandChanged()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            title = {
                TextWithIcon(
                    text = "Customer Details",
                    icon = Icons.Default.Person,
                    isTitle = true
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    onClick = {
                        onClickViewDetails(customer.customerId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "View Address Details",
                        tint = MaterialTheme.colors.secondaryVariant
                    )
                }
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                ) {
                    customer.customerName?.let {
                        TextWithIcon(
                            text = "Name: $it",
                            icon = Icons.Default.Person,
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }

                    TextWithIcon(
                        text = "Phone: ${customer.customerPhone}",
                        icon = Icons.Default.PhoneAndroid,
                    )

                    customer.customerEmail?.let {
                        if (it.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            TextWithIcon(
                                text = "Name: $it",
                                icon = Icons.Default.AlternateEmail,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        text = "Created At : ${customer.createdAt.toFormattedDateAndTime}",
                        icon = Icons.Default.MoreTime
                    )

                    customer.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextWithIcon(
                            text = "Updated At : ${it.toFormattedDateAndTime}",
                            icon = Icons.Default.Update
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))

                    Button(
                        onClick = {
                            onClickViewDetails(customer.customerId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(ButtonSize),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LightColor6,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Customer Details"
                        )
                        Spacer(modifier = Modifier.width(SpaceMini))
                        Text(
                            text = "View Customer Details".uppercase(),
                            style = MaterialTheme.typography.button,
                        )
                    }
                }
            },
        )
    }
}