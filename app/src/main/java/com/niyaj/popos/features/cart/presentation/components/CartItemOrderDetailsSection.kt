package com.niyaj.popos.features.cart.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.niyaj.popos.R
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.common.ui.theme.LightColor13
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.TextWithIcon

@Composable
fun CartItemOrderDetailsSection(
    modifier : Modifier = Modifier,
    orderId: String = "",
    customerPhone: String? = "",
    orderType: String = CartOrderType.DineIn.orderType,
    selected: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onViewClick: () -> Unit,
) {
    val height = if (orderType == CartOrderType.DineIn.orderType) 56.dp else 64.dp
    val iconColor = if(orderType == CartOrderType.DineIn.orderType) MaterialTheme.colors.primary else MaterialTheme.colors.secondaryVariant
    key(orderId) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .background(LightColor13, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                TextWithIcon(
                    text = orderId,
                    icon = Icons.Default.Tag,
                    isTitle = true,
                )

                customerPhone?.let {
                    Spacer(modifier = Modifier.height(SpaceMini))
                    TextWithIcon(
                        text = it,
                        icon = Icons.Default.PhoneAndroid,
                        isTitle = true,
                    )
                }
            }

            CartOrderDetailsButtons(
                selected = selected,
                iconColor = iconColor,
                onClick = onClick,
                onEditClick = onEditClick,
                onViewClick = onViewClick
            )
        }
    }
}


@Composable
fun CartOrderDetailsButtons(
    modifier : Modifier = Modifier,
    selected: Boolean,
    iconColor: Color,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onViewClick: () -> Unit,
) {
    Row(modifier) {
        IconButton(
            onClick = onEditClick
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Cart",
                tint = iconColor
            )
        }

        IconButton(
            onClick = onViewClick
        ) {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = stringResource(id = R.string.order_details),
                tint = iconColor,
            )
        }

        Crossfade(targetState = selected, label = "selected") {
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    imageVector = if (it) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = iconColor
                )
            }
        }
    }
}