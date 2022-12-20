package com.niyaj.popos.features.reports.data.repository

import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart.domain.repository.CartRepository
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.reports.domain.model.Reports
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import com.niyaj.popos.features.reports.domain.util.ProductWiseReport
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
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class ReportsRepositoryImpl(
    config: RealmConfiguration,
    private val cartRepository: CartRepository,
) : ReportsRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Report Session")
    }

    override suspend fun generateReport(startDate: String, endDate: String): Resource<Boolean> {
        return try {

            val itemReport = getItemsReport(startDate, endDate)
            val formattedDate = startDate.toSalaryDate

            realm.write {
                val existingReport = this.query<Reports>("reportDate == $0", formattedDate).first().find()

                //Update Report Data
                if (existingReport != null) {
                    //Expenses Quantity and Total Amount
                    existingReport.expensesQty = itemReport.first.first
                    existingReport.expensesAmount = itemReport.first.second

                    // DineIn Order Quantity and Total Amount
                    existingReport.dineInSalesQty = itemReport.second.first
                    existingReport.dineInSalesAmount = itemReport.second.second

                    // DineOut Order Quantity and Total Amount
                    existingReport.dineOutSalesQty = itemReport.third.first
                    existingReport.dineOutSalesAmount = itemReport.third.second

                    existingReport.updatedAt = System.currentTimeMillis().toString()

                }else { // Add New Report
                    val newReport = Reports()
                    newReport.reportId = BsonObjectId().toHexString()

                    newReport.reportDate = formattedDate

                    //Expenses Quantity and Total Amount
                    newReport.expensesQty = itemReport.first.first
                    newReport.expensesAmount = itemReport.first.second

                    // DineIn Order Quantity and Total Amount
                    newReport.dineInSalesQty = itemReport.second.first
                    newReport.dineInSalesAmount = itemReport.second.second

                    // DineOut Order Quantity and Total Amount
                    newReport.dineOutSalesQty = itemReport.third.first
                    newReport.dineOutSalesAmount = itemReport.third.second

                    this.copyToRealm(newReport)
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to generate report", false)
        }
    }

    override fun getReport(startDate: String): Resource<Reports?> {
        return try {
            val report = realm.query<Reports>("reportDate == $0", startDate.toSalaryDate).first().find()

            if (report == null){
                Resource.Success(Reports())
            }else {
                Resource.Success(report)
            }
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get report", null)
        }
    }

    override fun getReports(startDate: String): Flow<Resource<List<Reports>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val reports = realm.query<Reports>("createdAt <= $0", startDate).sort("_id", Sort.DESCENDING).asFlow()

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
                "createdAt >= $0 AND createdAt <= $1",
                startDate,
                endDate
            ).find().sumOf {
                it.expensesPrice.toInt()
            }

            val totalDineInItems = realm.query<CartOrder>(
                "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                OrderStatus.Processing.orderStatus,
                startDate,
                endDate,
                CartOrderType.DineIn.orderType
            ).find().sumOf {
                val price = cartRepository.countTotalPrice(it.cartOrderId)

                price.first.minus(price.second)
            }

            val totalDineOutItems = realm.query<CartOrder>(
                "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                OrderStatus.Processing.orderStatus,
                startDate,
                endDate,
                CartOrderType.DineOut.orderType
            ).find().sumOf {
                val price = cartRepository.countTotalPrice(it.cartOrderId)

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
    ): Flow<Resource<List<ProductWiseReport>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val cartOrders = realm.query<CartRealm>(
                    "cartOrder.cartOrderStatus != $0 AND cartOrder.updatedAt >= $1 AND cartOrder.updatedAt <= $2",
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

                val groupedProduct = cartOrders.groupBy({ it.product }) { it }

                val groupedProductWithQuantity = groupedProduct.map { cart ->
                    ProductWiseReport(
                        product = cart.key!!,
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
                "createdAt >= $0 AND createdAt <= $1",
                startDate,
                endDate
            ).find()

            val totalExpensesItem = expensesItem.size.toLong()
            val totalExpensesAmount = expensesItem.sumOf {
                it.expensesPrice.toLong()
            }

            //Get Today DineIn Orders
            val dineInItems = realm.query<CartOrder>(
                "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                OrderStatus.Processing.orderStatus,
                startDate,
                endDate,
                CartOrderType.DineIn.orderType
            ).find()

            val totalDineInItems = dineInItems.size.toLong()
            val totalDineInAmount = dineInItems.sumOf {
                val price = cartRepository.countTotalPrice(it.cartOrderId)

                price.first.minus(price.second)
            }.toLong()

            //Get DineOut Orders
            val dineOutItems = realm.query<CartOrder>(
                "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                OrderStatus.Processing.orderStatus,
                startDate,
                endDate,
                CartOrderType.DineOut.orderType
            ).find()

            val totalDineOutItems = dineOutItems.size.toLong()
            val totalDineOutAmount = dineOutItems.sumOf {
                val price = cartRepository.countTotalPrice(it.cartOrderId)

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
                    val reports = realm.query<Reports>("createdAt <= $0", date).find()

                    delete(reports)
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete last seven days before data", false)
        }
    }
}