package com.niyaj.feature.product.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.isSameDay
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.Cream
import com.niyaj.designsystem.theme.LightColor21
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.PoposPink300
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.product.details.ProductTotalOrderDetails
import com.niyaj.ui.components.LoadingIndicator

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductTotalOrdersDetails(
    details: ProductTotalOrderDetails,
    isLoading: Boolean = false,
) {
    Card(
        modifier = Modifier
            .testTag("CalculateSalary")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        if (isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMedium),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Total Orders",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
                Spacer(modifier = Modifier.height(SpaceSmall))
                Divider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceSmall))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = details.totalAmount.toRupee,
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("ProductTotalAmount")
                    )

                    val startDate = details.datePeriod.first
                    val endDate = details.datePeriod.second

                    if (startDate.isNotEmpty()) {
                        Card(
                            backgroundColor = LightColor21,
                            modifier = Modifier.testTag("DatePeriod")
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(SpaceMini),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = startDate.toBarDate,
                                    style = MaterialTheme.typography.body2,
                                )

                                if (endDate.isNotEmpty()) {
                                    if (!details.datePeriod.isSameDay()) {
                                        Spacer(modifier = Modifier.width(SpaceMini))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                                            contentDescription = "DatePeriod"
                                        )
                                        Spacer(modifier = Modifier.width(SpaceMini))
                                        Text(
                                            text = endDate.toBarDate,
                                            style = MaterialTheme.typography.body2,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
                Divider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceSmall))

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        backgroundColor = Cream,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("DineInSales")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceMini),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "DineIn Sales",
                                style = MaterialTheme.typography.body2,
                            )

                            Spacer(modifier = Modifier.height(SpaceMini))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceMini))

                            Text(
                                text = details.dineInAmount.toRupee,
                                style = MaterialTheme.typography.subtitle1,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(SpaceSmall))

                    Card(
                        backgroundColor = PoposPink300,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("DineOutSales")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceMini),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "DineOut Sales",
                                style = MaterialTheme.typography.body2,
                            )

                            Spacer(modifier = Modifier.height(SpaceMini))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceMini))

                            Text(
                                text = details.dineOutAmount.toRupee,
                                style = MaterialTheme.typography.subtitle1,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
                Divider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceSmall))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (details.mostOrderItemDate.isNotEmpty()) {
                        Card(
                            backgroundColor = LightColor6,
                            modifier = Modifier.testTag("MostOrders")
                        ) {
                            Text(
                                text = "Most Orders - ${details.mostOrderItemDate}",
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.padding(SpaceSmall)
                            )
                        }
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    }

                    if (details.mostOrderQtyDate.isNotEmpty()) {
                        Card(
                            backgroundColor = LightColor6,
                            modifier = Modifier.testTag("MostSales")
                        ) {
                            Text(
                                text = "Most Sales Qty - ${details.mostOrderQtyDate}",
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.padding(SpaceSmall)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}