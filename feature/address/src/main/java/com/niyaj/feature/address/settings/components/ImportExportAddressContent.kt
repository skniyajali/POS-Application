package com.niyaj.feature.address.settings.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.address.components.AddressCard
import com.niyaj.model.Address
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithIcon

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImportExportAddressContent(
    lazyListState: LazyGridState,
    addresses: List<Address>,
    selectedAddresses: List<String>,
    expanded: Boolean,
    onExpandChanged: () -> Unit,
    onSelectAddress: (String) -> Unit,
    onClickSelectAll: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
    ) {
        StandardExpandable(
            onExpandChanged = {
                onExpandChanged()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = expanded,
            title = {
                TextWithIcon(
                    text = if (selectedAddresses.isNotEmpty()) "${selectedAddresses.size} Selected" else "Select Addresses",
                    icon = Icons.Default.Dns,
                    isTitle = true
                )
            },
            rowClickable = true,
            trailing = {
                IconButton(
                    onClick = onClickSelectAll
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Rule,
                        contentDescription = "Select All Address"
                    )
                }
            },
            expand = { modifier: Modifier ->
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    items(
                        items = addresses,
                        key = { address ->
                            address.addressId
                        }
                    ) { address ->
                        AddressCard(
                            address = address,
                            doesSelected = {
                                selectedAddresses.contains(it)
                            },
                            onClick = onSelectAddress,
                            onLongClick = onSelectAddress,
                        )
                    }
                }
            }
        )
    }

    Spacer(modifier = Modifier.height(SpaceMedium))
}