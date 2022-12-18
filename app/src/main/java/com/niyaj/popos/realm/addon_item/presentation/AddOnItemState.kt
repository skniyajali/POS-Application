package com.niyaj.popos.realm.addon_item.presentation

import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.addon_item.domain.util.FilterAddOnItem
import com.niyaj.popos.realm.addon_item.domain.model.AddOnItem

data class AddOnItemState(
    val addOnItems: List<AddOnItem> = emptyList(),
    val filterAddOnItem: FilterAddOnItem = FilterAddOnItem.ByAddOnItemId(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)
