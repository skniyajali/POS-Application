package com.niyaj.popos.features.reports.domain.repository

import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reports.domain.model.Reports
import com.niyaj.popos.features.reports.domain.util.ProductWiseReport
import kotlinx.coroutines.flow.Flow

interface ReportsRepository {

    suspend fun generateReport(startDate: String, endDate: String): Resource<Boolean>

    fun getReport(startDate: String): Flow<Resource<Reports?>>

    fun getReports(startDate: String): Flow<Resource<List<Reports>>>

    suspend fun getTotalSales(startDate: String, endDate: String): Number

    suspend fun getProductWiseReport(startDate: String, endDate: String, orderType: String): Flow<Resource<List<ProductWiseReport>>>

    suspend fun getDineOutOrders(startDate: String, endDate: String): Flow<Resource<List<CartOrder>>>

    fun deleteLastSevenDaysBeforeData(): Resource<Boolean>
}