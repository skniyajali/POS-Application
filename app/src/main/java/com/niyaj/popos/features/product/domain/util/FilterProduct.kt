package com.niyaj.popos.features.product.domain.util

import com.niyaj.popos.features.common.util.SortType

sealed class FilterProduct(val sortType: SortType){

    class ByProductId(sortType: SortType): FilterProduct(sortType)

    class ByCategoryId(sortType: SortType): FilterProduct(sortType)

    class ByProductName(sortType: SortType): FilterProduct(sortType)

    class ByProductPrice(sortType: SortType): FilterProduct(sortType)

    class ByProductAvailability(sortType: SortType): FilterProduct(sortType)

    class ByProductQuantity(sortType: SortType): FilterProduct(sortType)

    class ByProductDate(sortType: SortType): FilterProduct(sortType)


    fun copy(sortType: SortType): FilterProduct {
        return when(this){
            is ByProductId -> ByProductId(sortType)
            is ByCategoryId -> ByCategoryId(sortType)
            is ByProductName -> ByProductName(sortType)
            is ByProductPrice -> ByProductPrice(sortType)
            is ByProductAvailability -> ByProductAvailability(sortType)
            is ByProductQuantity -> ByProductQuantity(sortType)
            is ByProductDate -> ByProductDate(sortType)
        }
    }
}
