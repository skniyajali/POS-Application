package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.model.ProductWiseReport
import com.niyaj.popos.domain.model.Reports
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ReportsRepository {

    suspend fun generateReport(startDate: String, endDate: String): Resource<Boolean>

    fun getReport(startDate: String): Resource<Reports?>

    fun getReports(startDate: String): Flow<Resource<List<Reports>>>

    suspend fun getProductWiseReport(startDate: String, endDate: String, orderType: String): Flow<Resource<List<ProductWiseReport>>>

    fun deleteLastSevenDaysBeforeData(): Resource<Boolean>
}