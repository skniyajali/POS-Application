package com.niyaj.popos.features.reports.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.common.ui.theme.KellyGreen
import com.niyaj.popos.features.common.ui.theme.MediumGray
import com.niyaj.popos.features.common.ui.theme.PurpleHaze
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.components.chart.common.dimens.ChartDimens
import com.niyaj.popos.features.components.chart.horizontalbar.HorizontalBarChart
import com.niyaj.popos.features.components.chart.horizontalbar.axis.HorizontalAxisConfig
import com.niyaj.popos.features.components.chart.horizontalbar.config.HorizontalBarConfig
import com.niyaj.popos.features.components.chart.horizontalbar.config.StartDirection
import com.niyaj.popos.features.reports.presentation.ProductWiseReportState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductWiseReport(
    productState: ProductWiseReportState,
    productRepExpanded: Boolean,
    selectedProduct: String,
    onExpandChanged: () -> Unit,
    onClickOrderType: (String) -> Unit,
    onBarClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = productRepExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                Column {
                    TextWithIcon(
                        text = "Product Wise Report",
                        icon = Icons.Default.Dns,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (selectedProduct.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        Text(
                            text = selectedProduct,
                            style = MaterialTheme.typography.body2,
                            color = MediumGray
                        )
                    }
                }
            },
            trailing = {
                OrderTypeDropdown(
                    text = productState.orderType.ifEmpty { "All" },
                    onItemClick = onClickOrderType
                )
            },
            expand = null,
            content = {
                Crossfade(targetState = productState, label = "ProductState") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()

                        state.data.isNotEmpty() -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            HorizontalBarChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((state.data.size.times(50)).dp)
                                    .padding(SpaceSmall),
                                onBarClick = {
                                    onBarClick(
                                        "${it.yValue} - ${
                                            it.xValue.toString().substringBefore(".")
                                        } Qty"
                                    )
                                },
                                colors = listOf(PurpleHaze, KellyGreen),
                                barDimens = ChartDimens(2.dp),
                                horizontalBarConfig = HorizontalBarConfig(
                                    showLabels = false,
                                    startDirection = StartDirection.Left
                                ),
                                horizontalAxisConfig = HorizontalAxisConfig(
                                    showAxes = true,
                                    showUnitLabels = false
                                ),
                                horizontalBarData = state.data,
                            )
                        }

                        else -> {
                            ItemNotAvailable(
                                text = state.error ?: "Product wise report not available",
                                showImage = false,
                            )
                        }
                    }
                }
            },
            contentDesc = "Product wise report"
        )
    }
}