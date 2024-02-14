package com.niyaj.domain.use_cases

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.data.repository.ReportsRepository
import com.niyaj.model.CustomerWiseReport
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCustomerWiseReport @Inject constructor(
    private val reportsRepository: ReportsRepository,
    private val customerRepository: CustomerRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(startDate: String): Flow<List<CustomerWiseReport>> {
        return withContext(ioDispatcher) {
            reportsRepository.getDineOutOrders(startDate).mapLatest { result ->
                val groupedByCustomer = result.groupBy { it.customer?.customerId }

                groupedByCustomer.map { groupedOrder ->
                    val customer = groupedOrder.key?.let {
                        customerRepository.getCustomerById(it)
                    }?.data

                    CustomerWiseReport(
                        customer,
                        groupedOrder.value.size
                    )
                }.sortedByDescending { it.orderQty }
            }
        }
    }
}