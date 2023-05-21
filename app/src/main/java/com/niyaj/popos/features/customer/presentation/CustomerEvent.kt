package com.niyaj.popos.features.customer.presentation


/**
 * Customer Event Class
 *
 * This class is used to handle all the events of the customer screen
 *  @see CustomerEvent
 *  @see CustomerEvent.CustomerNameChanged
 *  @see CustomerEvent.CustomerEmailChanged
 *  @see CustomerEvent.CustomerPhoneChanged
 *  @see CustomerEvent.SelectCustomer
 *  @see CustomerEvent.SelectAllCustomer
 *  @see CustomerEvent.DeselectAllCustomer
 *  @see CustomerEvent.CreateNewCustomer
 *  @see CustomerEvent.UpdateCustomer
 *  @see CustomerEvent.DeleteCustomer
 *  @see CustomerEvent.OnSearchCustomer
 *  @see CustomerEvent.ToggleSearchBar
 *  @see CustomerEvent.RefreshCustomer
 *  @see CustomerViewModel
 */
sealed class CustomerEvent{

    /**
     * Customer Name Changed Event Class
     * @param customerName [String]
     * @return [CustomerEvent]
     * @see CustomerEvent
     */
    data class CustomerNameChanged(val customerName: String) : CustomerEvent()

    /**
     * Customer Email Changed Event Class
     * @param customerEmail [String]
     * @return [CustomerEvent]
     * @see CustomerEvent
     */
    data class CustomerEmailChanged(val customerEmail: String) : CustomerEvent()

    /**
     * Customer Phone Changed Event Class
     * @param customerPhone [String]
     */
    data class CustomerPhoneChanged(val customerPhone: String) : CustomerEvent()

    /**
     * Select Customer Event Class
     * @param customerId [String]
     */
    data class SelectCustomer(val customerId: String) : CustomerEvent()

    /**
     * Select All Customer Event Object
     */
    object SelectAllCustomer : CustomerEvent()

    /**
     * Deselect All Customer Event Object
     */
    object DeselectAllCustomer : CustomerEvent()

    /**
     * Create New Customer Event Object
     */
    object CreateNewCustomer : CustomerEvent()

    /**
     * Update Customer Event Class
     * @param customerId [String]
     */
    data class UpdateCustomer(val customerId: String) : CustomerEvent()

    /**
     * Delete Customer Event Class
     * @param customers [List] of [String]
     * @return [CustomerEvent]
     */
    data class DeleteCustomer(val customers: List<String>) : CustomerEvent()

    /**
     * On Search Customer Event Class
     * @param searchText [String]
     */
    data class OnSearchCustomer(val searchText: String): CustomerEvent()

    /**
     * Toggle Search Bar Event Object
     * @return [CustomerEvent]
     */
    object ToggleSearchBar : CustomerEvent()

    /**
     * Refresh Customer Event Object
     */
    object RefreshCustomer : CustomerEvent()
}
