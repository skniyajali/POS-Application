package com.niyaj.popos.features.addon_item.presentation

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem

data class AddOnItemState(
    val addOnItems: List<AddOnItem> = emptyList(),
    val isLoading: Boolean = true,
    val error : String? = null
)