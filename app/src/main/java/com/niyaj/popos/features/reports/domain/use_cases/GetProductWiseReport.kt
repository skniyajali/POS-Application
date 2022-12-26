package com.niyaj.popos.features.reports.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.components.chart.horizontalbar.model.HorizontalBarData
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetProductWiseReport(
    private val reportsRepository: ReportsRepository
) {

    suspend operator fun invoke(startDate: String, endDate: String, orderType: String): Flow<Resource<List<HorizontalBarData>>>{
        return flow {
            reportsRepository.getProductWiseReport(startDate, endDate, orderType).collect { result ->
                when(result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
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

                            emit(Resource.Success(barData))
                        }
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get data from repository", emptyList()))
                    }
                }
            }
        }
    }
}