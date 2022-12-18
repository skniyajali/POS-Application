package com.niyaj.popos.domain.util.filter_items

import com.niyaj.popos.domain.util.SortType

sealed class FilterCategory(val sortType: SortType){

    class ByCategoryId(sortType: SortType): FilterCategory(sortType)

    class ByCategoryName(sortType: SortType): FilterCategory(sortType)

    class ByCategoryAvailability(sortType: SortType): FilterCategory(sortType)

    class ByCategoryDate(sortType: SortType): FilterCategory(sortType)


    fun copy(sortType: SortType): FilterCategory {
        return when(this){
            is ByCategoryId -> ByCategoryId(sortType)
            is ByCategoryName -> ByCategoryName(sortType)
            is ByCategoryAvailability -> ByCategoryAvailability(sortType)
            is ByCategoryDate -> ByCategoryDate(sortType)
        }
    }

}
