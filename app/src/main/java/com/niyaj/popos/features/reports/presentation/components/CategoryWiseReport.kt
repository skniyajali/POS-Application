package com.niyaj.popos.features.reports.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.reports.presentation.CategoryWiseReportState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryWiseReport(
    categoryState: CategoryWiseReportState,
    reportExpanded: Boolean,
    selectedCategory: String,
    onCategoryExpandChanged: (String) -> Unit,
    onExpandChanged: () -> Unit,
    onClickOrderType: (String) -> Unit,
    onProductClick: (productId: String) -> Unit
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
            expanded = reportExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                TextWithIcon(
                    text = "Category Wise Report",
                    icon = Icons.Default.Category,
                    isTitle = true
                )
            },
            trailing = {
                OrderTypeDropdown(
                    text = categoryState.orderType.ifEmpty { "All" }
                ) {
                    onClickOrderType(it)
                }
            },
            expand = null,
            content = {
                Crossfade(targetState = categoryState, label = "CategoryState") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.categoryWiseReport.isNotEmpty() -> {
                            CategoryWiseReportCard(
                                report = state.categoryWiseReport,
                                selectedCategory = selectedCategory,
                                onExpandChanged = onCategoryExpandChanged,
                                onProductClick = onProductClick
                            )
                        }

                        else -> {
                            ItemNotAvailable(
                                text = state.hasError ?: "Category wise report not available",
                                showImage = false,
                            )
                        }
                    }
                }
            },
            contentDesc = "Category wise report"
        )
    }
}