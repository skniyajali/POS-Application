package com.niyaj.popos.domain.util

sealed class SortType{
    object Ascending : SortType()
    object Descending : SortType()
}
