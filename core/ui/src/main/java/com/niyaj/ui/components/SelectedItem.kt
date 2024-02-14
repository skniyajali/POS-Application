package com.niyaj.ui.components

import androidx.compose.runtime.Immutable

@Immutable
data class SelectedItem(
    val selectedItems: List<String> = emptyList()
)
