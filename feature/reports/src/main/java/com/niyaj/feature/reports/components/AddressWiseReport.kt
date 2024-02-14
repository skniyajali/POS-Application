package com.niyaj.feature.reports.components

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
import androidx.compose.material.icons.filled.Business
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.reports.AddressWiseReportState
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithIcon

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddressWiseReport(
    addressState: AddressWiseReportState,
    addressWiseRepExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onAddressClick: (addressId: String) -> Unit,
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
            expanded = addressWiseRepExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                TextWithIcon(
                    text = "Address Wise Report",
                    icon = Icons.Default.Business,
                    isTitle = true
                )
            },
            trailing = {
                CountBox(count = addressState.reports.size.toString())
            },
            rowClickable = true,
            expand = null,
            content = {
                Crossfade(targetState = addressState, label = "AddressState") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.reports.isNotEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.reports.forEachIndexed { index, report ->
                                    if (report.address != null) {
                                        AddressReportCard(
                                            report = report,
                                            onAddressClick = onAddressClick
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
                                text = state.error ?: "Address wise report not available",
                                showImage = false
                            )
                        }
                    }
                }
            },
            contentDesc = "Address wise report"
        )
    }
}