package com.niyaj.popos.features.order.domain.util

import com.niyaj.popos.features.common.util.SortType

sealed class FilterOrder(val sortType: SortType){

    class ByCustomerAddress(sortType: SortType): FilterOrder(sortType)

    class ByCustomerName(sortType: SortType): FilterOrder(sortType)

    class ByOrderPrice(sortType: SortType): FilterOrder(sortType)

    class ByOrderType(sortType: SortType): FilterOrder(sortType)

    class ByOrderStatus(sortType: SortType): FilterOrder(sortType)

    class ByUpdatedDate(sortType: SortType): FilterOrder(sortType)


    fun copy(sortType: SortType): FilterOrder {
        return when(this){
            is ByCustomerAddress -> ByCustomerAddress(sortType)
            is ByCustomerName -> ByCustomerName(sortType)
            is ByOrderPrice -> ByOrderPrice(sortType)
            is ByOrderType -> ByOrderType(sortType)
            is ByOrderStatus -> ByOrderStatus(sortType)
            is ByUpdatedDate -> ByUpdatedDate(sortType)
        }
    }
}