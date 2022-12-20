package com.niyaj.popos.features.address.domain.util

import com.niyaj.popos.features.common.util.SortType

sealed class FilterAddress(val sortType: SortType){

    class ByAddressId(sortType: SortType): FilterAddress(sortType)

    class ByShortName(sortType: SortType): FilterAddress(sortType)

    class ByAddressName(sortType: SortType): FilterAddress(sortType)

    class ByAddressDate(sortType: SortType): FilterAddress(sortType)

    fun copy(sortType: SortType): FilterAddress {
        return when(this){
            is ByAddressId -> ByAddressId(sortType)
            is ByShortName -> ByShortName(sortType)
            is ByAddressName -> ByAddressName(sortType)
            is ByAddressDate -> ByAddressDate(sortType)
        }
    }
}
