package com.niyaj.popos.features.charges.domain.util

import com.niyaj.popos.features.common.util.SortType

sealed class FilterCharges(val sortType: SortType){

    class ByChargesId(sortType: SortType): FilterCharges(sortType)

    class ByChargesName(sortType: SortType): FilterCharges(sortType)

    class ByChargesPrice(sortType: SortType): FilterCharges(sortType)

    class ByChargesApplicable(sortType: SortType): FilterCharges(sortType)

    class ByChargesDate(sortType: SortType): FilterCharges(sortType)


    fun copy(sortType: SortType): FilterCharges {
        return when(this){
            is ByChargesId -> ByChargesId(sortType)
            is ByChargesName -> ByChargesName(sortType)
            is ByChargesPrice -> ByChargesPrice(sortType)
            is ByChargesApplicable -> ByChargesApplicable(sortType)
            is ByChargesDate -> ByChargesDate(sortType)
        }
    }
}
