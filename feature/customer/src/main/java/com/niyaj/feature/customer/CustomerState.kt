package com.niyaj.feature.customer

import com.niyaj.model.Customer

/**
 * Contains the state of the customer screen
 * @param customers List of customers
 * @param isLoading Boolean to indicate if the screen is loading
 * @param error String to indicate if there is an error
 * @constructor Creates a new CustomerState
 */
data class CustomerState(
    val customers: List<Customer> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
