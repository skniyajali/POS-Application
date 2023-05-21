package com.niyaj.popos.features.reports.domain.use_cases

import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reports.domain.model.AddressWiseReport
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class GetAddressWiseReport(
    private val reportsRepository: ReportsRepository,
    private val addressRepository: AddressRepository,
) {
    operator fun invoke(startDate: String, endDate: String): Flow<Resource<List<AddressWiseReport>>> {
        return channelFlow {
            withContext(Dispatchers.IO){
                reportsRepository.getDineOutOrders(startDate, endDate).collectLatest { result ->
                    when (result){
                        is Resource.Loading -> {
                            send(Resource.Loading(result.isLoading))
                        }
                        is Resource.Success -> {
                            result.data?.let { cartOrders ->
                                val groupedByAddress = cartOrders.groupBy { it.address?.addressId }

                                val data = groupedByAddress.map { groupedOrder ->
                                    val address = groupedOrder.key?.let {
                                        addressRepository.getAddressById(it)
                                    }?.data

                                    AddressWiseReport(
                                        address,
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