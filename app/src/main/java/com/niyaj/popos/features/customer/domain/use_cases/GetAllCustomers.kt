package com.niyaj.popos.features.customer.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import com.niyaj.popos.features.customer.domain.util.FilterCustomer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class GetAllCustomers(
    private val customerRepository: CustomerRepository
) {
    operator fun invoke(
        filterCustomer: FilterCustomer = FilterCustomer.ByCustomerId(SortType.Ascending),
        searchText: String = "",
    ): Flow<Resource<List<Customer>>> {
        return channelFlow {
            withContext(Dispatchers.IO) {
                customerRepository.getAllCustomers().collectLatest { result ->
                    when(result) {
                        is Resource.Loading -> {
                            send(Resource.Loading(result.isLoading))
                        }
                        is Resource.Success -> {
                            val data = result.data?.let { customers ->
                                when(filterCustomer.sortType) {
                                    SortType.Ascending -> {
                                        when (filterCustomer){
                                            is FilterCustomer.ByCustomerId -> { customers.sortedBy { it.customerId } }
                                            is FilterCustomer.ByCustomerName -> { customers.sortedBy { it.customerName } }
                                            is FilterCustomer.ByCustomerEmail -> { customers.sortedBy { it.customerEmail } }
                                            is FilterCustomer.ByCustomerPhone -> { customers.sortedBy { it.customerPhone } }
                                            is FilterCustomer.ByCustomerDate -> { customers.sortedBy { it.createdAt } }
                                        }
                                    }
                                    SortType.Descending -> {
                                        when (filterCustomer){
                                            is FilterCustomer.ByCustomerId -> { customers.sortedByDescending { it.customerId } }
                                            is FilterCustomer.ByCustomerName -> { customers.sortedByDescending { it.customerName } }
                                            is FilterCustomer.ByCustomerEmail -> { customers.sortedByDescending { it.customerEmail } }
                                            is FilterCustomer.ByCustomerPhone -> { customers.sortedByDescending { it.customerPhone } }
                                            is FilterCustomer.ByCustomerDate -> { customers.sortedByDescending { it.createdAt } }
                                        }
                                    }
                                }.filter { customer ->
                                    if(searchText.isNotEmpty()){
                                        customer.customerEmail?.contains(searchText, true) == true ||
                                                customer.customerPhone.contains(searchText, true) ||
                                                customer.customerName?.contains(searchText, true) == true
                                    }else{
                                        true
                                    }
                                }
                            }

                            send(Resource.Success(data))
                        }
                        is Resource.Error -> {
                            send(Resource.Error(result.message ?: "Unable to get customers from repository"))
                        }
                    }
                }
            }
        }
    }
}