package com.niyaj.popos.features.reports.data.repository

import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart.domain.repository.CartRepository
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.reports.domain.model.Reports
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import com.niyaj.popos.features.reports.domain.util.ProductWiseReport
import com.niyaj.popos.utils.getCalculatedStartDate
import com.niyaj.popos.utils.toSalaryDate
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedObject
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class ReportsRepositoryImpl(
    config: RealmConfiguration,
    private val cartRepository: CartRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReportsRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Report Session")
    }

    override suspend fun generateReport(startDate: String, endDate: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
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
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to generate report", false)
        }
    }

    override fun getReport(startDate: String): Flow<Resource<Reports?>> {
        return channelFlow {
            withContext(ioDispatcher){
                try {
                    val report = realm.query<Reports>("reportDate == $0", startDate.toSalaryDate).first().asFlow()

                    report.collect { changes ->
                        when(changes){
                            is InitialObject -> {
                                send(Resource.Success(changes.obj))
                            }
                            is UpdatedObject -> {
                                send(Resource.Success(changes.obj))
                            }
                            else -> {
                                send(Resource.Success(null))
                            }
                        }
                    }

                }catch (e: Exception) {
                    send(Resource.Error(message = e.message ?: "Unable to get report", data = null))
                }
            }
        }
    }

    override fun getReports(startDate: String): Flow<Resource<List<Reports>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val reports = realm.query<Reports>("createdAt <= $0", startDate).sort("reportId", Sort.DESCENDING).asFlow()

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
                send(Resource.Loading(false))
                send(Resource.Error(
                    message = e.message ?: "Unable to get reports",
                    data = emptyList()
                ))
            }
        }
    }

    override suspend fun getTotalSales(startDate: String, endDate: String): Number {
        try {
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

            return (totalDineInItems + totalDineOutItems)
        } catch (e: Exception) {
            Timber.d(e)
            return 0
        }
    }

    override suspend fun getProductWiseReport(
        startDate: String,
        endDate: String,
        orderType: String
    ): Flow<Resource<List<ProductWiseReport>>> {
        return channelFlow {
            withContext(ioDispatcher){
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

                    val groupedProduct = cartOrders.groupBy({ it.product?.productId }) { it }

                    val groupedProductWithQuantity = groupedProduct.map { cart ->
                        ProductWiseReport(
                            product = getProduct(cart.key!!),
                            quantity = cart.value.sumOf {
                                it.quantity
                            }
                        )
                    }

                    send(Resource.Success(groupedProductWithQuantity))

                    send(Resource.Loading(false))

                } catch (e: Exception) {
                    send(Resource.Loading(false))
                    send(Resource.Error(
                        message = e.message ?: "Unable to get data from database",
                        data = emptyList()
                    ))
                }
            }
        }
    }

    override fun deleteLastSevenDaysBeforeData(): Resource<Boolean> {
        return try {

            val date = getCalculatedStartDate("-7")

            CoroutineScope(ioDispatcher).launch {
                realm.write {
                    val reports = realm.query<Reports>("createdAt <= $0", date).find()

                    delete(reports)
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Unable to delete last seven days before data",
                data = false
            )
        }
    }

    override suspend fun getDineOutOrders(startDate: String, endDate: String): Flow<Resource<List<CartOrder>>> {
        return channelFlow {
            withContext(ioDispatcher){
                try {
                    send(Resource.Loading(true))

                    val cartOrders = realm.query<CartOrder>(
                        "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                        OrderStatus.Processing.orderStatus,
                        startDate,
                        endDate,
                        CartOrderType.DineOut.orderType
                    ).find().asFlow()

                    cartOrders.collectLatest {changes ->
                        when(changes){
                            is UpdatedResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                            is InitialResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                }catch (e: Exception) {
                    send(Resource.Error(e.message ?: "Unable to get orders", data = emptyList()))
                }
            }
        }
    }

    private fun getProduct(productId: String): Product {
        return realm.query<Product>("productId == $0", productId).find().first()
    }

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
                val price = countTotalPrice(it.cartOrderId)

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
                val price = countTotalPrice(it.cartOrderId)

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

    private fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
        var totalPrice = 0
        var discountPrice = 0

        val cartOrder = realm.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
        val cartOrders = realm.query<CartRealm>("cartOrder.cartOrderId == $0", cartOrderId).find()

        if (cartOrder != null && cartOrders.isNotEmpty()) {
            if (cartOrder.doesChargesIncluded) {
                val charges = realm.query<Charges>().find()
                for (charge in charges) {
                    if (charge.isApplicable && cartOrder.orderType != CartOrderType.DineIn.orderType) {
                        totalPrice += charge.chargesPrice
                    }
                }
            }

            if (cartOrder.addOnItems.isNotEmpty()) {
                for (addOnItem in cartOrder.addOnItems) {

                    totalPrice += addOnItem.itemPrice

                    if (!addOnItem.isApplicable) {
                        discountPrice += addOnItem.itemPrice
                    }
                }
            }

            for (cartOrder1 in cartOrders) {
                if (cartOrder1.product != null) {
                    totalPrice += cartOrder1.quantity.times(cartOrder1.product?.productPrice!!)
                }
            }
        }

        return Pair(totalPrice, discountPrice)
    }

}