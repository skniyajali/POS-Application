package com.niyaj.feature.cart.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.Cream2
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.OrderType

@Composable
fun CartItemTotalPriceSection(
    modifier: Modifier = Modifier,
    itemCount: Int = 0,
    totalPrice: Int = 0,
    discountPrice: Int = 0,
    orderType: OrderType = OrderType.DineIn,
    showPrintBtn: Boolean = true,
    onClickPlaceOrder: () -> Unit = {},
    onClickPrintOrder: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Cream2, RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
            .padding(SpaceSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.subtitle1,
                color = if (orderType == OrderType.DineOut)
                    MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.secondary
            )

            Text(
                modifier = Modifier.weight(0.8f),
                text = "Rs. ${totalPrice.minus(discountPrice)}",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold,
                color = if (orderType == OrderType.DineOut)
                    MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.secondary
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { onClickPlaceOrder() },
                enabled = itemCount > 0,
                shape = CutCornerShape(4.dp),
                border = BorderStroke(
                    1.dp,
                    if (orderType == OrderType.DineOut)
                        MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.secondary
                )
            ) {
                Text(
                    text = "Place Order".uppercase(),
                    style = MaterialTheme.typography.button,
                    color = if (orderType == OrderType.DineOut)
                        MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.secondary
                )
            }

            if (showPrintBtn) {
                Spacer(modifier = Modifier.width(SpaceSmall))

                IconButton(
                    onClick = {
                        onClickPrintOrder()
                    },
                    enabled = itemCount > 0,
                    modifier = Modifier
                        .background(
                            if (orderType == OrderType.DineOut)
                                MaterialTheme.colors.secondaryVariant
                            else MaterialTheme.colors.secondary,
                            CutCornerShape(4.dp)
                        )
                        .heightIn(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Print Order",
                        tint = MaterialTheme.colors.onSecondary,
                    )
                }
            }
        }
    }
}