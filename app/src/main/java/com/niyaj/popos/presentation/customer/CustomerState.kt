package com.niyaj.popos.presentation.customer

import com.niyaj.popos.domain.model.Customer
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterCustomer

data class CustomerState(
    val customers: List<Customer> = emptyList(),
    val filterCustomer: FilterCustomer = FilterCustomer.ByCustomerId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)
