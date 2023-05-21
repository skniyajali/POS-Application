package com.niyaj.popos.features.customer.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.model.filterCustomer
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class GetAllCustomers(
    private val customerRepository : CustomerRepository
) {
    operator fun invoke(
        searchText : String = "",
    ) : Flow<Resource<List<Customer>>> {
        return channelFlow {
            withContext(Dispatchers.IO) {
                customerRepository.getAllCustomers().collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            send(Resource.Loading(result.isLoading))
                        }

                        is Resource.Success -> {
                            val data = result.data?.let { customers ->
                                customers.filter { customer ->
                                    customer.filterCustomer(searchText = searchText)
                                }
                            }

                            send(Resource.Success(data))
                        }

                        is Resource.Error -> {
                            send(
                                Resource.Error(
                                    result.message ?: "Unable to get customers from repository"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}