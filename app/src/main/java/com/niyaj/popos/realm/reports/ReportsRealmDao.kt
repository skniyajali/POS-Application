package com.niyaj.popos.realm.reports

import com.niyaj.popos.domain.model.ProductWiseReportRealm
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ReportsRealmDao {

    suspend fun generateReport(startDate: String, endDate: String): Resource<Boolean>

    fun getReport(startDate: String): Resource<ReportsRealm?>

    fun getReports(startDate: String): Flow<Resource<List<ReportsRealm>>>

    suspend fun getTotalSales(startDate: String, endDate: String): Number

    suspend fun getProductWiseReport(startDate: String, endDate: String, orderType: String): Flow<Resource<List<ProductWiseReportRealm>>>

    fun deleteLastSevenDaysBeforeData(): Resource<Boolean>
}