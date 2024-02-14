package com.niyaj.feature.reports.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddressWiseReport
import com.niyaj.model.CustomerWiseReport
import com.niyaj.model.ProductWiseReport
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithIcon

@Composable
fun CustomerReportCard(
    modifier: Modifier = Modifier,
    customerReport : CustomerWiseReport,
    onClickCustomer: (String) -> Unit,
) {
    customerReport.customer?.let { customer ->
        Row (
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClickCustomer(customer.customerId) }
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                customer.customerName?.let {
                    TextWithIcon(
                        text = it,
                        icon = Icons.Default.Person,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(SpaceMini))
                }

                TextWithIcon(
                    text = customer.customerPhone,
                    icon = Icons.Default.PhoneAndroid,
                    fontWeight = FontWeight.SemiBold,
                )

                customer.customerEmail?.let { email ->
                    Spacer(modifier = Modifier.height(SpaceMini))
                    TextWithIcon(
                        text = email,
                        icon = Icons.Default.Email,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpaceMini))

            Text(
                text = customerReport.orderQty.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.secondaryVariant,
            )
        }
    }
}

@Composable
fun AddressReportCard(
    report: AddressWiseReport,
    onAddressClick: (String) -> Unit,
) {
    report.address?.let { address ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddressClick(address.addressId) }
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = address.addressName,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(2F)
            )

            Text(
                text = address.shortName,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.5F)
            )

            Text(
                text = report.orderQty.toString(),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.weight(0.5F)
            )
        }
    }
}

@Composable
fun ProductReportCard(
    report: ProductWiseReport,
    onProductClick: (String) -> Unit,
) {
    report.product?.let { product ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onProductClick(
                        product.productId
                    )
                }
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = product.productName,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = report.quantity.toString(),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.weight(0.5F)
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryWiseReportCard(
    report: List<ProductWiseReport>,
    selectedCategory: String,
    onExpandChanged: (String) -> Unit,
    onProductClick: (String) -> Unit,
) {
    val groupedByCategoryWiseReport = report
        .groupBy { it.product?.category?.categoryName }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        groupedByCategoryWiseReport.forEach { (category, products) ->
            if (category != null && products.isNotEmpty()) {
                val totalQuantity = products.sumOf { it.quantity }.toString()
                StandardExpandable(
                    modifier = Modifier
                        .fillMaxWidth(),
                    expanded = category == selectedCategory,
                    onExpandChanged = {
                        onExpandChanged(category)
                    },
                    title = {
                        TextWithIcon(
                            text = category,
                            icon = Icons.Default.Category,
                            isTitle = true
                        )
                    },
                    trailing = {
                        CountBox(count = totalQuantity)
                    },
                    rowClickable = true,
                    expand = { modifier : Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                onExpandChanged(category)
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
                        val sortedProducts = products.sortedByDescending { it.quantity }
                        Column {
                            sortedProducts.forEachIndexed { index, productWithQty ->
                                ProductReportCard(
                                    report = productWithQty,
                                    onProductClick = onProductClick
                                )

                                if (index != products.size - 1) {
                                    Spacer(modifier = Modifier.height(SpaceMini))
                                    Divider(modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(SpaceMini))
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(SpaceMini))
                Divider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceMini))
            }
        }
    }
}