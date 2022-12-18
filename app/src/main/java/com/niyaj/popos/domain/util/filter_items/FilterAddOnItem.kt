package com.niyaj.popos.domain.util.filter_items

import com.niyaj.popos.domain.util.SortType

sealed class FilterAddOnItem(val sortType: SortType){

    class ByAddOnItemId(sortType: SortType): FilterAddOnItem(sortType)

    class ByAddOnItemName(sortType: SortType): FilterAddOnItem(sortType)

    class ByAddOnItemPrice(sortType: SortType): FilterAddOnItem(sortType)

    class ByAddOnItemDate(sortType: SortType): FilterAddOnItem(sortType)


    fun copy(sortType: SortType): FilterAddOnItem {
        return when(this){
            is ByAddOnItemId -> ByAddOnItemId(sortType)
            is ByAddOnItemName -> ByAddOnItemName(sortType)
            is ByAddOnItemPrice -> ByAddOnItemPrice(sortType)
            is ByAddOnItemDate -> ByAddOnItemDate(sortType)
        }
    }
}
