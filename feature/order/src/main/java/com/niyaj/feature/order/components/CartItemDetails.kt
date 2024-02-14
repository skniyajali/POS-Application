package com.niyaj.feature.order.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CartOrder
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.OrderType
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.StandardOutlinedChip
import com.niyaj.ui.components.TextDivider
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.components.ThreeGridTexts
import com.niyaj.ui.components.TwoGridTexts

/**
 * This composable displays the cart items
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CartItemDetails(
    cartOrder: CartOrder,
    cartProduct: List<CartProductItem>,
    charges: List<Charges>,
    orderPrice: Pair<Int, Int>,
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
                    text = "Cart Items",
                    icon = Icons.Default.ShoppingBag,
                    isTitle = true
                )
            },
            trailing = {
                StandardOutlinedChip(
                    text = "${cartProduct.size} Items",
                    isToggleable = false,
                    isSelected = false,
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
                    ThreeGridTexts(
                        textOne = "Name",
                        textTwo = "Price",
                        textThree = "Qty",
                        isTitle = true
                    )
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Divider(modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    cartProduct.forEach { product ->
                        ThreeGridTexts(
                            textOne = product.productName,
                            textTwo = product.productPrice.toString().toRupee,
                            textThree = product.productQuantity.toString(),
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }

                    if (cartOrder.addOnItems.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextDivider(
                            text = "Add On Items"
                        )

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        for (addOnItem in cartOrder.addOnItems) {
                            TwoGridTexts(
                                textOne = addOnItem.itemName,
                                textTwo = addOnItem.itemPrice.toString().toRupee,
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }

                    if (cartOrder.doesChargesIncluded && cartOrder.orderType != OrderType.DineIn) {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextDivider(text = "Charges")

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        for (charge in charges) {
                            if (charge.isApplicable) {
                                TwoGridTexts(
                                    textOne = charge.chargesName,
                                    textTwo = charge.chargesPrice.toString().toRupee,
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                            }
                        }

                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Divider(modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Sub Total",
                            style = MaterialTheme.typography.body1,
                        )

                        Text(
                            text = orderPrice.first.toString().toRupee,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Discount",
                            style = MaterialTheme.typography.body1
                        )

                        Text(
                            text = orderPrice.second.toString().toRupee,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp)

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold,
                        )

                        Text(
                            text = (orderPrice.first.minus(orderPrice.second)).toString().toRupee,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            },
        )
    }
}