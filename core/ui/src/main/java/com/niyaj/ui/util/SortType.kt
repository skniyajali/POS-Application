package com.niyaj.ui.util

sealed class SortType{
    data object Ascending : SortType()
    data object Descending : SortType()
}
