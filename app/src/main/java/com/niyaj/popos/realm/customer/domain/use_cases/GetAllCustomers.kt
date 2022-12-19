package com.niyaj.popos.realm.customer.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.customer.domain.model.Customer
import com.niyaj.popos.realm.customer.domain.repository.CustomerRepository
import com.niyaj.popos.realm.customer.domain.util.FilterCustomer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllCustomers(
    private val customerRepository: CustomerRepository
) {

    suspend operator fun invoke(
        filterCustomer: FilterCustomer = FilterCustomer.ByCustomerId(SortType.Ascending),
        searchText: String = "",
    ): Flow<Resource<List<Customer>>> {
        return flow {
            customerRepository.getAllCustomers().collect { result ->
                when(result) {
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { customers ->
                                when(filterCustomer.sortType) {
                                    SortType.Ascending -> {
                                        when (filterCustomer){
                                            is FilterCustomer.ByCustomerId -> { customers.sortedBy { it.customerId } }
                                            is FilterCustomer.ByCustomerName -> { customers.sortedBy { it.customerName } }
                                            is FilterCustomer.ByCustomerEmail -> { customers.sortedBy { it.customerEmail } }
                                            is FilterCustomer.ByCustomerPhone -> { customers.sortedBy { it.customerPhone } }
                                            is FilterCustomer.ByCustomerDate -> { customers.sortedBy { it.created_at } }
                                        }
                                    }
                                    SortType.Descending -> {
                                        when (filterCustomer){
                                            is FilterCustomer.ByCustomerId -> { customers.sortedByDescending { it.customerId } }
                                            is FilterCustomer.ByCustomerName -> { customers.sortedByDescending { it.customerName } }
                                            is FilterCustomer.ByCustomerEmail -> { customers.sortedByDescending { it.customerEmail } }
                                            is FilterCustomer.ByCustomerPhone -> { customers.sortedByDescending { it.customerPhone } }
                                            is FilterCustomer.ByCustomerDate -> { customers.sortedByDescending { it.created_at } }
                                        }
                                    }
                                }.filter { customer ->
                                    if(searchText.isNotEmpty()){
                                        customer.customerEmail?.contains(searchText, true) == true ||
                                        customer.customerPhone.contains(searchText, true) ||
                                        customer.customerName?.contains(searchText, true) == true ||
                                        customer.created_at.contains(searchText, true) ||
                                        customer.updated_at?.contains(searchText, true) == true
                                    }else{
                                        true
                                    }
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get customers from repository"))
                    }
                }
            }
        }
    }
}