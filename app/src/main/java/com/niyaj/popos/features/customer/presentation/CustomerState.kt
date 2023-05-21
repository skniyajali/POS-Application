package com.niyaj.popos.features.customer.presentation

import com.niyaj.popos.features.customer.domain.model.Customer

/**
 * Contains the state of the customer screen
 * @param customers List of customers
 * @param isLoading Boolean to indicate if the screen is loading
 * @param error String to indicate if there is an error
 * @constructor Creates a new CustomerState
 */
data class CustomerState(
    val customers: List<Customer> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
