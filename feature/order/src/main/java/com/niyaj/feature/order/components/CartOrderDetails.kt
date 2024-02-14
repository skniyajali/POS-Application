package com.niyaj.feature.order.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Update
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CartOrder
import com.niyaj.model.OrderType
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.StandardOutlinedChip
import com.niyaj.ui.components.TextWithIcon

/**
 * This composable displays the cart order details
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CartOrderDetails(
    cartOrder: CartOrder,
    doesExpanded: Boolean,
    onExpandChanged: () -> Unit,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                TextWithIcon(
                    text = "Order Details",
                    icon = Icons.Default.Inventory,
                    isTitle = true
                )
            },
            trailing = {
                StandardOutlinedChip(
                    text = cartOrder.cartOrderStatus.name,
                    isSelected = false,
                    isToggleable = false,
                    onClick = {}
                )
            },
            rowClickable = true,
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall)
                ) {

                    TextWithIcon(
                        text = cartOrder.orderId,
                        icon = Icons.Default.Tag
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        text = "Order Type : ${cartOrder.orderType}",
                        icon = if (cartOrder.orderType == OrderType.DineIn)
                            Icons.Default.RoomService else Icons.Default.DeliveryDining
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        text = "Created At : ${cartOrder.createdAt.toFormattedDateAndTime}",
                        icon = Icons.Default.MoreTime
                    )

                    cartOrder.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextWithIcon(
                            text = "Updated At : ${it.toFormattedDateAndTime}",
                            icon = Icons.Default.Update
                        )
                    }
                }
            },
        )
    }
}