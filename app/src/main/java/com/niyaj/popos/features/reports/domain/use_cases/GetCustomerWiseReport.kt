package com.niyaj.popos.features.reports.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import com.niyaj.popos.features.reports.domain.model.CustomerWiseReport
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class GetCustomerWiseReport(
    private val reportsRepository: ReportsRepository,
    private val customerRepository: CustomerRepository
) {
    operator fun invoke(startDate: String, endDate: String): Flow<Resource<List<CustomerWiseReport>>> {
        return channelFlow {
            withContext(Dispatchers.IO) {
                reportsRepository.getDineOutOrders(startDate, endDate).collectLatest { result ->
                    when (result){
                        is Resource.Loading -> {
                            send(Resource.Loading(result.isLoading))
                        }
                        is Resource.Success -> {
                            result.data?.let { cartOrders ->
                                val groupedByCustomer = cartOrders.groupBy { it.customer?.customerId }

                                val data = groupedByCustomer.map { groupedOrder ->
                                    val customer = groupedOrder.key?.let {
                                        customerRepository.getCustomerById(it)
                                    }?.data

                                    CustomerWiseReport(
                                        customer,
                                        groupedOrder.value.size
                                    )
                                }.sortedByDescending { it.orderQty }

                                send(Resource.Success(data))
                            }

                        }
                        is Resource.Error -> {
                            send(Resource.Error(result.message ?: "Unable to get data from repository."))
                        }
                    }
                }
            }
        }
    }
}