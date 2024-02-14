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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Update
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Address
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithIcon

/**
 * This composable displays the address details
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddressDetails(
    address: Address,
    doesExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onClickViewDetails: (String) -> Unit,
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
                    text = "Address Details",
                    icon = Icons.Default.LocationOn,
                    isTitle = true
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    onClick = {
                        onClickViewDetails(address.addressId)
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
                    TextWithIcon(
                        text = "Short Name: ${address.shortName}",
                        icon = Icons.Default.Business,
                    )
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        text = "Name: ${address.addressName}",
                        icon = Icons.Default.Home,
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        text = "Created At : ${address.createdAt.toFormattedDateAndTime}",
                        icon = Icons.Default.MoreTime
                    )

                    address.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextWithIcon(
                            text = "Updated At : ${it.toFormattedDateAndTime}",
                            icon = Icons.Default.Update
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))

                    Button(
                        onClick = {
                            onClickViewDetails(address.addressId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(ButtonSize),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondaryVariant,
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Details,
                            contentDescription = "Address Details"
                        )
                        Spacer(modifier = Modifier.width(SpaceMini))
                        Text(
                            text = "View Address Details".uppercase(),
                            style = MaterialTheme.typography.button,
                        )
                    }
                }
            },
        )
    }
}