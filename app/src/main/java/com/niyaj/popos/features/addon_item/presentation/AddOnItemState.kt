package com.niyaj.popos.features.addon_item.presentation

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.util.FilterAddOnItem
import com.niyaj.popos.features.common.util.SortType

data class AddOnItemState(
    val addOnItems: List<AddOnItem> = emptyList(),
    val filterAddOnItem: FilterAddOnItem = FilterAddOnItem.ByAddOnItemId(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)
