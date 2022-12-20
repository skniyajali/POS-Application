package com.niyaj.popos.features.customer.presentation

import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.util.FilterCustomer

data class CustomerState(
    val customers: List<Customer> = emptyList(),
    val filterCustomer: FilterCustomer = FilterCustomer.ByCustomerId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)
