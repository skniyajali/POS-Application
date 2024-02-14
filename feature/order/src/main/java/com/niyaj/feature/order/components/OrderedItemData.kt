package com.niyaj.feature.order.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import com.niyaj.common.utils.toFormattedTime
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.TextWithIcon

@Composable
fun OrderedItemData(
    shape: Shape,
    orderId: String,
    orderPrice: String,
    orderDate: String? = null,
    customerPhone: String? = null,
    customerAddress: String? = null,
    onClickViewDetails: () -> Unit = {},
    onClickPrintOrder: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = shape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextWithIcon(
                        text = orderId,
                        icon = Icons.Default.Tag
                    )

                    customerPhone?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextWithIcon(
                            text = customerPhone,
                            icon = Icons.Default.PhoneAndroid
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    orderDate?.let {
                        TextWithIcon(
                            text = orderDate.toFormattedTime,
                            icon = Icons.Default.AccessTime
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    customerAddress?.let {
                        TextWithIcon(
                            text = customerAddress,
                            icon = Icons.Default.Place
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }

                    TextWithIcon(
                        text = orderPrice,
                        icon = Icons.Default.CurrencyRupee
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClickViewDetails
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = stringResource(id = R.string.order_details),
                            tint = MaterialTheme.colors.primary,
                        )
                    }

                    Spacer(modifier = Modifier.width(SpaceMini))

                    IconButton(
                        onClick = onClickPrintOrder
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = stringResource(id = R.string.print_order),
                            tint = MaterialTheme.colors.primary,
                        )
                    }
                }
            }
        }
    }
}