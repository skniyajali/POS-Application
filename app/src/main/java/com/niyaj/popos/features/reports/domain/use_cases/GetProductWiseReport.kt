package com.niyaj.popos.features.reports.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.components.chart.horizontalbar.model.HorizontalBarData
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class GetProductWiseReport(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(startDate: String, endDate: String, orderType: String): Flow<Resource<List<HorizontalBarData>>>{
        return channelFlow {
            withContext(Dispatchers.IO) {
                reportsRepository.getProductWiseReport(startDate, endDate, orderType).collectLatest { result ->
                    when(result){
                        is Resource.Loading -> {
                            send(Resource.Loading(result.isLoading))
                        }
                        is Resource.Success -> {
                            result.data?.let { data ->
                                val barData = data.map {
                                    HorizontalBarData(
                                        xValue = it.quantity.toFloat(),
                                        yValue = it.product?.productName!!
                                    )
                                }.sortedByDescending {
                                    it.xValue
                                }

                                send(Resource.Success(barData))
                            }
                        }
                        is Resource.Error -> {
                            send(Resource.Error(result.message ?: "Unable to get data from repository", emptyList()))
                        }
                    }
                }
            }
        }
    }
}