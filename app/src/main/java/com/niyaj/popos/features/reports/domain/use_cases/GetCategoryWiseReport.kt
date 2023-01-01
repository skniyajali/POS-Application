package com.niyaj.popos.features.reports.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import com.niyaj.popos.features.reports.domain.util.ProductWiseReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext

class GetCategoryWiseReport(
    private val reportsRepository: ReportsRepository
) {
    operator fun invoke(startDate: String, endDate: String, orderType: String): Flow<Resource<List<ProductWiseReport>>> {
        return channelFlow {
            withContext(Dispatchers.IO){
                reportsRepository.getProductWiseReport(startDate, endDate, orderType).collect { result ->
                    when(result){
                        is Resource.Loading -> {
                            send(Resource.Loading(result.isLoading))
                        }
                        is Resource.Success -> {
                            result.data?.let { data ->
                                send(Resource.Success(data))
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