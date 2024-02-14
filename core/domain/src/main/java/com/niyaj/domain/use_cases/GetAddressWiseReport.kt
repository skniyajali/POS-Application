package com.niyaj.domain.use_cases

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.repository.AddressRepository
import com.niyaj.data.repository.ReportsRepository
import com.niyaj.model.AddressWiseReport
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAddressWiseReport @Inject constructor(
    private val reportsRepository: ReportsRepository,
    private val addressRepository: AddressRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(startDate: String): Flow<List<AddressWiseReport>> {
        return withContext(ioDispatcher) {
            reportsRepository.getDineOutOrders(startDate).mapLatest { result ->
                val groupedByAddress = result.groupBy { it.address?.addressId }

                groupedByAddress.map { groupedOrder ->
                    val address = groupedOrder.key?.let {
                        addressRepository.getAddressById(it)
                    }?.data

                    AddressWiseReport(
                        address,
                        groupedOrder.value.size
                    )

                }.sortedByDescending { it.orderQty }
            }
        }
    }
}