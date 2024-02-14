package com.niyaj.feature.reports.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.MediumGray
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.reports.ReportsBarState
import com.niyaj.ui.chart.common.dimens.ChartDimens
import com.niyaj.ui.chart.horizontalbar.HorizontalBarChart
import com.niyaj.ui.chart.horizontalbar.axis.HorizontalAxisConfig
import com.niyaj.ui.chart.horizontalbar.config.HorizontalBarConfig
import com.niyaj.ui.chart.horizontalbar.config.StartDirection
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator

@Composable
fun ReportBarData(
    reportBarState: ReportsBarState,
    selectedBarData: String,
    onBarClick: (String) -> Unit,
    onClickViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 2.dp,
    ) {
        Crossfade(
            targetState = reportBarState,
            label = "ReportBarState"
        ) { state ->
            when {
                state.isLoading -> LoadingIndicator()

                state.reportBarData.isNotEmpty() -> {
                    val reportBarData = state.reportBarData

                    Column(
                        modifier = Modifier
                            .padding(SpaceSmall)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(
                                    text = "Last ${reportBarData.size} Days Reports",
                                    style = MaterialTheme.typography.body1,
                                    fontWeight = FontWeight.Bold
                                )
                                if (selectedBarData.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    Text(
                                        text = selectedBarData,
                                        style = MaterialTheme.typography.body2,
                                        color = MediumGray
                                    )
                                }
                            }

                            IconButton(
                                onClick = onClickViewDetails
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                                    contentDescription = "View Reports Details",
                                    tint = MaterialTheme.colors.secondary
                                )
                            }
                        }

                        Divider(modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        HorizontalBarChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((reportBarData.size.times(60)).dp)
                                .padding(SpaceSmall),
                            onBarClick = {
                                onBarClick(
                                    "${it.yValue} - ${
                                        it.xValue.toString().substringBefore(".").toRupee
                                    }"
                                )
                            },
                            colors = listOf(MaterialTheme.colors.secondary, MaterialTheme.colors.secondaryVariant),
                            barDimens = ChartDimens(2.dp),
                            horizontalBarConfig = HorizontalBarConfig(
                                showLabels = false,
                                startDirection = StartDirection.Left,
                                productReport = false
                            ),
                            horizontalAxisConfig = HorizontalAxisConfig(
                                showAxes = true,
                                showUnitLabels = false
                            ),
                            horizontalBarData = reportBarData,
                        )
                    }
                }

                else -> {
                    ItemNotAvailable(
                        modifier = Modifier.padding(SpaceSmall),
                        text = state.error ?: "Reports are not available",
                        showImage = false,
                    )
                }
            }
        }
    }
}