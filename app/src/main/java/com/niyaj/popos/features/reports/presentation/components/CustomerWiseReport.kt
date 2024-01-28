package com.niyaj.popos.features.reports.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.CountBox
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.reports.presentation.CustomerWiseReportState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomerWiseReport(
    customerState: CustomerWiseReportState,
    customerWiseRepExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onCustomerClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
    ) {
        StandardExpandable(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            expanded = customerWiseRepExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                TextWithIcon(
                    text = "Customer Wise Report",
                    icon = Icons.Default.PeopleAlt,
                    isTitle = true
                )
            },
            trailing = {
                CountBox(count = customerState.reports.size.toString())
            },
            rowClickable = true,
            expand = null,
            content = {
                Crossfade(targetState = customerState, label = "CustomerState") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.reports.isNotEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.reports.forEachIndexed { index, report ->
                                    if (report.customer != null) {
                                        CustomerReportCard(
                                            customerReport = report,
                                            onClickCustomer = onCustomerClick
                                        )

                                        if (index != state.reports.size - 1) {
                                            Spacer(modifier = Modifier.height(SpaceMini))
                                            Divider(modifier = Modifier.fillMaxWidth())
                                            Spacer(modifier = Modifier.height(SpaceMini))
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            ItemNotAvailable(
                                text = "Customer wise report not available",
                                showImage = false
                            )
                        }
                    }
                }
            },
            contentDesc = "Customer wise report"
        )
    }
}