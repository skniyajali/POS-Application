package com.niyaj.feature.product.components

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
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toDate
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Product
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithIcon

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductDetails(
    product: Product,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickEdit: () -> Unit,
) {
    Card(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("ProductDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                TextWithIcon(
                    text = "Product Details",
                    icon = Icons.AutoMirrored.Filled.Feed,
                    isTitle = true
                )
            },
            trailing = {
                IconButton(
                    onClick = onClickEdit
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Employee",
                        tint = MaterialTheme.colors.primary
                    )
                }
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
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
                        modifier = Modifier.testTag(product.productName),
                        text = "Name - ${product.productName}",
                        icon = Icons.Default.CollectionsBookmark
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        modifier = Modifier.testTag(product.productPrice.toString()),
                        text = "Price - ${product.productPrice.toString().toRupee}",
                        icon = Icons.Default.CurrencyRupee
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        modifier = Modifier.testTag(product.category?.categoryName ?: "Category"),
                        text = "Category - ${product.category?.categoryName}",
                        icon = Icons.Default.Category
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        modifier = Modifier.testTag(product.productAvailability.toString()),
                        text = "Availability : ${product.productAvailability}",
                        icon = if (product.productAvailability)
                            Icons.Default.RadioButtonChecked
                        else Icons.Default.RadioButtonUnchecked
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        modifier = Modifier.testTag(product.createdAt.toDate),
                        text = "Created At : ${product.createdAt.toFormattedDateAndTime}",
                        icon = Icons.Default.CalendarToday
                    )

                    product.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        TextWithIcon(
                            text = "Updated At : ${it.toFormattedDateAndTime}",
                            icon = Icons.AutoMirrored.Filled.Login
                        )
                    }
                }
            },
        )
    }
}