package com.niyaj.feature.order.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.LightColor12
import com.niyaj.designsystem.theme.SpaceSmall
import de.charlex.compose.RevealSwipe

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderedItem(
    orderId: String,
    orderPrice: String,
    orderDate: String? = null,
    customerPhone: String? = null,
    customerAddress: String? = null,
    onClickPrintOrder: () -> Unit = {},
    onMarkedAsProcessing: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    onClickViewDetails: () -> Unit = {},
    onClickEdit: () -> Unit = {},
) {
    RevealSwipe(
        modifier = Modifier
            .fillMaxWidth(),
        onContentClick = onClickViewDetails,
        maxRevealDp = 150.dp,
        hiddenContentStart = {
            IconButton(
                onClick = onMarkedAsProcessing
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 25.dp),
                )
            }

            Spacer(modifier = Modifier.width(SpaceSmall))

            IconButton(
                onClick = onClickEdit
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
        },
        hiddenContentEnd = {
            IconButton(
                onClick = onClickDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete order",
                    modifier = Modifier.padding(horizontal = 25.dp),
                )
            }
        },
        contentColor = MaterialTheme.colors.primary,
        backgroundCardContentColor = LightColor12,
        backgroundCardStartColor = MaterialTheme.colors.primary,
        backgroundCardEndColor = MaterialTheme.colors.error,
        shape = RoundedCornerShape(6.dp),
        backgroundStartActionLabel = "Start",
        backgroundEndActionLabel = "End",
    ) {
        OrderedItemData(
            shape = it,
            orderId = orderId,
            orderPrice = orderPrice,
            orderDate = orderDate,
            customerPhone = customerPhone,
            customerAddress = customerAddress,
            onClickViewDetails = onClickViewDetails,
            onClickPrintOrder = onClickPrintOrder
        )
    }
}