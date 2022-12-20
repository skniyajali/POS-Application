package com.niyaj.popos.features.reports.domain.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reports.domain.model.Reports
import com.niyaj.popos.features.reports.domain.util.ProductWiseReport
import kotlinx.coroutines.flow.Flow

interface ReportsRepository {

    suspend fun generateReport(startDate: String, endDate: String): Resource<Boolean>

    fun getReport(startDate: String): Resource<Reports?>

    fun getReports(startDate: String): Flow<Resource<List<Reports>>>

    suspend fun getTotalSales(startDate: String, endDate: String): Number

    suspend fun getProductWiseReport(startDate: String, endDate: String, orderType: String): Flow<Resource<List<ProductWiseReport>>>

    fun deleteLastSevenDaysBeforeData(): Resource<Boolean>
}