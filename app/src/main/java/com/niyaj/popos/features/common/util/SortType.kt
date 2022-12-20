package com.niyaj.popos.features.common.util

sealed class SortType{
    object Ascending : SortType()
    object Descending : SortType()
}
