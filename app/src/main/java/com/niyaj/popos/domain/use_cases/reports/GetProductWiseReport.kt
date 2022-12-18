package com.niyaj.popos.domain.use_cases.reports

import com.niyaj.popos.domain.repository.ReportsRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.presentation.components.chart.horizontalbar.model.HorizontalBarData
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
                            }

                            emit(Resource.Success(barData))
                        }
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get data from repository"))
                    }
                }
            }
        }
    }
}