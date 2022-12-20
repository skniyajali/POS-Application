package com.niyaj.popos.features.customer.domain.util

import com.niyaj.popos.features.common.util.SortType

sealed class FilterCustomer(val sortType: SortType) {

    class ByCustomerId(sortType: SortType): FilterCustomer(sortType)

    class ByCustomerName(sortType: SortType): FilterCustomer(sortType)

    class ByCustomerPhone(sortType: SortType): FilterCustomer(sortType)

    class ByCustomerEmail(sortType: SortType): FilterCustomer(sortType)

    class ByCustomerDate(sortType: SortType): FilterCustomer(sortType)


    fun copy(sortType: SortType): FilterCustomer {
        return when(this){
            is ByCustomerId -> ByCustomerId(sortType)
            is ByCustomerName -> ByCustomerName(sortType)
            is ByCustomerEmail -> ByCustomerEmail(sortType)
            is ByCustomerPhone -> ByCustomerPhone(sortType)
            is ByCustomerDate -> ByCustomerDate(sortType)
        }
    }
}
