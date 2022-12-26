package com.niyaj.popos.features.reports.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.components.chart.horizontalbar.model.HorizontalBarData
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import com.niyaj.popos.util.formattedDateToStartMillis
import com.niyaj.popos.util.toBarDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class GetReportsBarData (
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(startDate: String): Flow<Resource<List<HorizontalBarData>>> {
        return channelFlow {
            reportsRepository.getReports(startDate).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let {
                            it.map {report ->
                                val totalAmount = report.expensesAmount.plus(report.dineInSalesAmount).plus(report.dineOutSalesAmount)

                                HorizontalBarData(
                                    xValue = totalAmount.toFloat(),
                                    yValue = formattedDateToStartMillis(report.reportDate).toBarDate
                                )
                            }
                        }

                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get data from database"))
                    }
                }
            }
        }
    }
}