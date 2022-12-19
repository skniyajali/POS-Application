package com.niyaj.popos.realm.customer.presentation

import com.niyaj.popos.realm.customer.domain.model.Customer
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.customer.domain.util.FilterCustomer

data class CustomerState(
    val customers: List<Customer> = emptyList(),
    val filterCustomer: FilterCustomer = FilterCustomer.ByCustomerId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)
