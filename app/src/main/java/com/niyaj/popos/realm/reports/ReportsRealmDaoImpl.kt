package com.niyaj.popos.realm.reports

import com.niyaj.popos.domain.model.ProductWiseReportRealm
import com.niyaj.popos.domain.util.CartOrderType
import com.niyaj.popos.domain.util.OrderStatus
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.cart.CartRealm
import com.niyaj.popos.realm.cart.CartRealmDao
import com.niyaj.popos.realm.cart_order.CartOrderRealm
import com.niyaj.popos.realm.expenses.domain.model.Expenses
import com.niyaj.popos.util.getCalculatedStartDate
import com.niyaj.popos.util.toSalaryDate
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ReportsRealmDaoImpl(
    config: RealmConfiguration,
    private val cartRealmDao: CartRealmDao,
) : ReportsRealmDao {

    val realm = Realm.open(config)

    init {
        Timber.d("Report Session")
    }

    override suspend fun generateReport(startDate: String, endDate: String): Resource<Boolean> {
        return try {

            val itemReport = getItemsReport(startDate, endDate)
            val formattedDate = startDate.toSalaryDate

            realm.write {
                val currentDateReport = this.query<ReportsRealm>("reportDate == $0", formattedDate).first().find()

                //Update Report Data
                if (currentDateReport != null) {
                    //Expenses Quantity and Total Amount
                    currentDateReport.expensesQty = itemReport.first.first
                    currentDateReport.expensesAmount = itemReport.first.second

                    // DineIn Order Quantity and Total Amount
                    currentDateReport.dineInSalesQty = itemReport.second.first
                    currentDateReport.dineInSalesAmount = itemReport.second.second

                    // DineOut Order Quantity and Total Amount
                    currentDateReport.dineOutSalesQty = itemReport.third.first
                    currentDateReport.dineOutSalesAmount = itemReport.third.second

                    currentDateReport.updatedAt = System.currentTimeMillis().toString()

                }else { // Add New Report
                    val todayReport = ReportsRealm()

                    todayReport.reportDate = formattedDate

                    //Expenses Quantity and Total Amount
                    todayReport.expensesQty = itemReport.first.first
                    todayReport.expensesAmount = itemReport.first.second

                    // DineIn Order Quantity and Total Amount
                    todayReport.dineInSalesQty = itemReport.second.first
                    todayReport.dineInSalesAmount = itemReport.second.second

                    // DineOut Order Quantity and Total Amount
                    todayReport.dineOutSalesQty = itemReport.third.first
                    todayReport.dineOutSalesAmount = itemReport.third.second

                    this.copyToRealm(todayReport)
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to generate report", false)
        }
    }

    override fun getReport(startDate: String): Resource<ReportsRealm?> {
        return try {
            val report = realm.query<ReportsRealm>("reportDate == $0", startDate.toSalaryDate).first().find()

            if (report == null){
                Resource.Success(ReportsRealm())
            }else {
                Resource.Success(report)
            }
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get report", null)
        }
    }

    override fun getReports(startDate: String): Flow<Resource<List<ReportsRealm>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val reports = realm.query<ReportsRealm>("createdAt <= $0", startDate).sort("_id", Sort.DESCENDING).asFlow()

                reports.collect { report ->
                    when(report) {
                        is InitialResults -> {
                            send(Resource.Success(report.list))
                            send(Resource.Loading(false))
                        }
                        is UpdatedResults -> {
                            send(Resource.Success(report.list))
                            send(Resource.Loading(false))
                        }
                    }
                }
            }catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get reports"))
            }
        }
    }

    override suspend fun getTotalSales(startDate: String, endDate: String): Number {
        try {
            val totalExpensesItem = realm.query<Expenses>(
                "created_at >= $0 AND created_at <= $1",
                startDate,
                endDate
            ).find().sumOf {
                it.expensesPrice.toInt()
            }

            val totalDineInItems = realm.query<CartOrderRealm>(
                "cartOrderStatus != $0 AND updated_at >= $1 AND updated_at <= $2 AND orderType == $3",
                OrderStatus.Processing.orderStatus,
                startDate,
                endDate,
                CartOrderType.DineIn.orderType
            ).find().sumOf {
                val price = cartRealmDao.countTotalPrice(it._id)

                price.first.minus(price.second)
            }

            val totalDineOutItems = realm.query<CartOrderRealm>(
                "cartOrderStatus != $0 AND updated_at >= $1 AND updated_at <= $2 AND orderType == $3",
                OrderStatus.Processing.orderStatus,
                startDate,
                endDate,
                CartOrderType.DineOut.orderType
            ).find().sumOf {
                val price = cartRealmDao.countTotalPrice(it._id)

                price.first.minus(price.second)
            }


            return (totalExpensesItem + totalDineInItems + totalDineOutItems)
        } catch (e: Exception) {
            Timber.d(e)
            throw e
        }
    }

    override suspend fun getProductWiseReport(
        startDate: String,
        endDate: String,
        orderType: String
    ): Flow<Resource<List<ProductWiseReportRealm>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val cartOrders = realm.query<CartRealm>(
                    "cartOrder.cartOrderStatus != $0 AND cartOrder.updated_at >= $1 AND cartOrder.updated_at <= $2",
                    OrderStatus.Processing.orderStatus,
                    startDate,
                    endDate,
                ).find().filter {
                    if (orderType.isNotEmpty()){
                        it.cartOrder?.orderType == orderType
                    }else{
                        true
                    }
                }

                val groupedProduct = cartOrders.groupBy({ it.product?._id }) { it }

                val groupedProductWithQuantity = groupedProduct.map { cart ->
                    ProductWiseReportRealm(
                        productId = cart.key!!,
                        quantity = cart.value.sumOf {
                            it.quantity
                        }
                    )
                }

                send(Resource.Success(groupedProductWithQuantity))

                send(Resource.Loading(false))

            } catch (e: Exception) {
                Timber.e(e)
                send(Resource.Error(e.message ?: "Unable to get data from database"))
            }
        }
    }

    /**
     * @param startDate
     * @param endDate
     * This method will return [Triple]-[Pair] values of
     * *Expenses* Size and Total Amount,
     * *DineIn Order* Quantity and Total Amount,
     * *DineOut Order* Quantity and Total Amount
     * as sequence on specific date.
     */
    private fun getItemsReport(startDate: String, endDate: String): Triple<Pair<Long, Long>, Pair<Long, Long>, Pair<Long, Long>> {
        try {
            //Get Today Expenses
            val expensesItem = realm.query<Expenses>(
                "created_at >= $0 AND created_at <= $1",
                startDate,
                endDate
            ).find()

            val totalExpensesItem = expensesItem.size.toLong()
            val totalExpensesAmount = expensesItem.sumOf {
                it.expensesPrice.toLong()
            }

            //Get Today DineIn Orders
            val dineInItems = realm.query<CartOrderRealm>(
                "cartOrderStatus != $0 AND updated_at >= $1 AND updated_at <= $2 AND orderType == $3",
                OrderStatus.Processing.orderStatus,
                startDate,
                endDate,
                CartOrderType.DineIn.orderType
            ).find()

            val totalDineInItems = dineInItems.size.toLong()
            val totalDineInAmount = dineInItems.sumOf {
                val price = cartRealmDao.countTotalPrice(it._id)

                price.first.minus(price.second)
            }.toLong()

            //Get DineOut Orders
            val dineOutItems = realm.query<CartOrderRealm>(
                "cartOrderStatus != $0 AND updated_at >= $1 AND updated_at <= $2 AND orderType == $3",
                OrderStatus.Processing.orderStatus,
                startDate,
                endDate,
                CartOrderType.DineOut.orderType
            ).find()

            val totalDineOutItems = dineOutItems.size.toLong()
            val totalDineOutAmount = dineOutItems.sumOf {
                val price = cartRealmDao.countTotalPrice(it._id)

                price.first.minus(price.second)
            }.toLong()


            return Triple(
                Pair(totalExpensesItem, totalExpensesAmount),
                Pair(totalDineInItems, totalDineInAmount),
                Pair(totalDineOutItems, totalDineOutAmount)
            )
        } catch (e: Exception) {
            Timber.e(e)
            return Triple(Pair(0,0), Pair(0,0), Pair(0,0))
        }
    }

    /**
     * Delete last 7 days before data from current date.
     */
    override fun deleteLastSevenDaysBeforeData(): Resource<Boolean> {
        return try {

            val date = getCalculatedStartDate("-7")

            CoroutineScope(Dispatchers.IO).launch {
                realm.write {
                    val reports = realm.query<ReportsRealm>("createdAt <= $0", date).find()

                    delete(reports)
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete last seven days before data", false)
        }
    }
}