package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.ProductWiseReport
import com.niyaj.popos.domain.model.Reports
import com.niyaj.popos.domain.repository.ProductRepository
import com.niyaj.popos.domain.repository.ReportsRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.reports.ReportsRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow

class ReportsRepositoryImpl(
    private val reportsRealmDao: ReportsRealmDao,
    private val productRepository: ProductRepository
) : ReportsRepository {

    override suspend fun generateReport(
        startDate: String,
        endDate: String
    ): Resource<Boolean> {
        return reportsRealmDao.generateReport(startDate, endDate)
    }

    override fun getReport(startDate: String): Resource<Reports?> {
        val result = reportsRealmDao.getReport(startDate)

        return result.data?.let {report ->
            Resource.Success(
                Reports(
                    reportId = report._id,
                    expensesQty = report.expensesQty,
                    expensesAmount = report.expensesAmount,
                    dineInSalesQty = report.dineInSalesQty,
                    dineInSalesAmount = report.dineInSalesAmount,
                    dineOutSalesQty = report.dineOutSalesQty,
                    dineOutSalesAmount = report.dineOutSalesAmount,
                    reportDate = report.reportDate,
                    createdAt = report.createdAt,
                    updatedAt = report.updatedAt
                )
            )
        } ?: Resource.Error(result.message ?: "Unable to get report")
    }

    override fun getReports(startDate: String): Flow<Resource<List<Reports>>> {
        return channelFlow {
            reportsRealmDao.getReports(startDate).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let {
                            it.map {report ->
                                Reports(
                                    reportId = report._id,
                                    expensesQty = report.expensesQty,
                                    expensesAmount = report.expensesAmount,
                                    dineInSalesQty = report.dineInSalesQty,
                                    dineInSalesAmount = report.dineInSalesAmount,
                                    dineOutSalesQty = report.dineOutSalesQty,
                                    dineOutSalesAmount = report.dineOutSalesAmount,
                                    reportDate = report.reportDate,
                                    createdAt = report.createdAt,
                                    updatedAt = report.updatedAt
                                )
                            }
                        }

                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get reports"))
                    }
                }
            }
        }
    }

    override fun deleteLastSevenDaysBeforeData(): Resource<Boolean> {
        return reportsRealmDao.deleteLastSevenDaysBeforeData()
    }

    override suspend fun getProductWiseReport(
        startDate: String,
        endDate: String,
        orderType: String,
    ): Flow<Resource<List<ProductWiseReport>>> {
        return flow {
            reportsRealmDao.getProductWiseReport(startDate, endDate, orderType).collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        result.data?.let { listProducts ->
                            val mappedProduct = listProducts.map {
                                ProductWiseReport(
                                    product = productRepository.getProductById(it.productId).data,
                                    quantity = it.quantity
                                )
                            }.sortedByDescending { it.quantity }

                            emit(Resource.Success(mappedProduct))
                        }

                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get data from database"))
                    }
                }
            }
        }
    }
}