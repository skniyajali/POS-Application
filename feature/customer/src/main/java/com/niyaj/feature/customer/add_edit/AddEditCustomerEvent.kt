package com.niyaj.feature.customer.add_edit


/**
 * Customer Event Class
 *
 * This class is used to handle all the events of the customer screen
 *  @see AddEditCustomerEvent
 *  @see AddEditCustomerEvent.CustomerNameChanged
 *  @see AddEditCustomerEvent.CustomerEmailChanged
 *  @see AddEditCustomerEvent.CustomerPhoneChanged
 *  @see AddEditCustomerEvent.CreateOrUpdateCustomer
 */
sealed interface AddEditCustomerEvent {
    /**
     * Customer Name Changed Event Class
     * @param customerName [String]
     * @return [AddEditCustomerEvent]
     * @see AddEditCustomerEvent
     */
    data class CustomerNameChanged(val customerName: String) : AddEditCustomerEvent

    /**
     * Customer Email Changed Event Class
     * @param customerEmail [String]
     * @return [AddEditCustomerEvent]
     * @see AddEditCustomerEvent
     */
    data class CustomerEmailChanged(val customerEmail: String) : AddEditCustomerEvent

    /**
     * Customer Phone Changed Event Class
     * @param customerPhone [String]
     */
    data class CustomerPhoneChanged(val customerPhone: String) : AddEditCustomerEvent


    data object CreateOrUpdateCustomer : AddEditCustomerEvent

}
