package com.niyaj.popos.features.address.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddressCard(
    address: Address,
    doesSelected: (String) -> Boolean,
    doesAnySelected: Boolean,
    onSelectAddress: (String) -> Unit,
    onClickAddress: (String) -> Unit = {}
) {
    key(address.addressId) {
        Card(
            modifier = Modifier
                .testTag(address.addressName)
                .fillMaxWidth()
                .padding(SpaceMini)
                .combinedClickable(
                    onClick = {
                        if (!doesAnySelected) {
                            onClickAddress(address.addressId)
                        } else {
                            onSelectAddress(address.addressId)
                        }
                    },
                    onLongClick = {
                        onSelectAddress(address.addressId)
                    }
                ),
            elevation = 2.dp,
            border = if(doesSelected(address.addressId))
                BorderStroke(1.dp, MaterialTheme.colors.primary)
            else null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = address.addressName,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Text(
                        text = address.shortName,
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.background),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = address.addressName,
                        tint = MaterialTheme.colors.secondaryVariant,
                    )
                }
            }
        }
    }
}