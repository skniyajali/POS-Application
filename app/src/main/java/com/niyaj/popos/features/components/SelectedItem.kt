package com.niyaj.popos.features.components

import androidx.compose.runtime.Immutable

@Immutable
data class SelectedItem(
    val selectedItems: List<String> = emptyList()
)
