package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.CartOrder
import com.niyaj.model.OrderType
import com.niyaj.model.ProductWiseReport
import com.niyaj.model.Reports
import kotlinx.coroutines.flow.Flow

interface ReportsRepository {

    suspend fun generateReport(startDate: String): Resource<Boolean>

    suspend fun getReport(startDate: String): Flow<Reports?>

    suspend fun getReports(startDate: String): Flow<List<Reports>>

    suspend fun getTotalSales(startDate: String): Number

    suspend fun getProductWiseReport(startDate: String, orderType: OrderType?): Flow<List<ProductWiseReport>>

    suspend fun getDineOutOrders(startDate: String): Flow<List<CartOrder>>

    suspend fun deleteLastSevenDaysBeforeData(): Resource<Boolean>
}