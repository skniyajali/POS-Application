package com.niyaj.popos.presentation.add_on_items

import com.niyaj.popos.domain.model.AddOnItem
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterAddOnItem

data class AddOnItemState(
    val addOnItems: List<AddOnItem> = emptyList(),
    val filterAddOnItem: FilterAddOnItem = FilterAddOnItem.ByAddOnItemId(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)
