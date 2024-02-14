package com.niyaj.data.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.getCalculatedEndDate
import com.niyaj.common.utils.getCalculatedStartDate
import com.niyaj.common.utils.toSalaryDate
import com.niyaj.data.repository.CartRepository
import com.niyaj.data.repository.ReportsRepository
import com.niyaj.data.utils.collectAndSend
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.ExpensesEntity
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.ReportsEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.CartOrder
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.model.ProductWiseReport
import com.niyaj.model.Reports
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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class ReportsRepositoryImpl(
    config: RealmConfiguration,
    private val cartRepository: CartRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ReportsRepository {

    val realm = Realm.open(config)

    override suspend fun generateReport(startDate: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val endDate = getCalculatedEndDate(date = startDate)
                val itemReport = getItemsReport(startDate, endDate)

                val formattedDate = startDate.toSalaryDate

                realm.write {
                    val existingReport =
                        this.query<ReportsEntity>("reportDate == $0", formattedDate).first().find()

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

                    } else { // Add New Report
                        val newReport = ReportsEntity()
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
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to generate report")
        }
    }

    override suspend fun getReport(startDate: String): Flow<Reports?> {
        return channelFlow {
            withContext(ioDispatcher) {

                try {
                    val report = realm
                        .query<ReportsEntity>("reportDate == $0", startDate.toSalaryDate)
                        .first().asFlow()

                    report.collect { changes ->
                        when (changes) {
                            is InitialObject -> {
                                send(changes.obj.toExternalModel())
                            }

                            is UpdatedObject -> {
                                send(changes.obj.toExternalModel())
                            }

                            else -> {
                                send(null)
                            }
                        }
                    }

                } catch (e: Exception) {
                    send(null)
                }
            }
        }
    }

    override suspend fun getReports(startDate: String): Flow<List<Reports>> {
        return channelFlow {
            try {
                val endDate = getCalculatedEndDate(date = startDate)

                val reports = realm.query<ReportsEntity>("createdAt <= $0", endDate)
                    .sort("reportId", Sort.DESCENDING).asFlow()

                reports.collect { report ->
                    when (report) {
                        is InitialResults -> {
                            send(report.list.map { it.toExternalModel() })
                        }

                        is UpdatedResults -> {
                            send(report.list.map { it.toExternalModel() })
                        }
                    }
                }
            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    override suspend fun getTotalSales(startDate: String): Number {
        return withContext(ioDispatcher) {
            try {
                val endDate = getCalculatedEndDate(date = startDate)

                val totalDineInItems = async(ioDispatcher) {
                    realm.query<CartOrderEntity>(
                        "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                        OrderStatus.PROCESSING.name,
                        startDate,
                        endDate,
                        OrderType.DineIn.name
                    ).find().sumOf {
                        val price = cartRepository.countTotalPrice(it.cartOrderId)

                        price.first.minus(price.second)
                    }
                }

                val totalDineOutItems = async(ioDispatcher) {
                    realm.query<CartOrderEntity>(
                        "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                        OrderStatus.PROCESSING.name,
                        startDate,
                        endDate,
                        OrderType.DineOut.name
                    ).find().sumOf {
                        val price = cartRepository.countTotalPrice(it.cartOrderId)

                        price.first.minus(price.second)
                    }
                }

                (totalDineInItems.await() + totalDineOutItems.await())
            } catch (e: Exception) {
                0
            }
        }
    }

    override suspend fun getProductWiseReport(
        startDate: String,
        orderType: OrderType?
    ): Flow<List<ProductWiseReport>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val endDate = getCalculatedEndDate(date = startDate)

                    val cartOrders = realm.query<CartEntity>(
                        "cartOrder.cartOrderStatus != $0 AND cartOrder.updatedAt >= $1 AND cartOrder.updatedAt <= $2",
                        OrderStatus.PROCESSING.name,
                        startDate,
                        endDate,
                    ).find().filter {
                        if (orderType != null) {
                            it.cartOrder?.orderType == orderType.name
                        } else {
                            true
                        }
                    }

                    val groupedProduct = cartOrders.groupBy({ it.product?.productId }) { it }

                    val groupedProductWithQuantity = groupedProduct.map { cart ->
                        ProductWiseReport(
                            product = getProduct(cart.key!!).toExternalModel(),
                            quantity = cart.value.sumOf {
                                it.quantity
                            }
                        )
                    }

                    send(groupedProductWithQuantity)
                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    override suspend fun deleteLastSevenDaysBeforeData(): Resource<Boolean> {
        return try {

            val date = getCalculatedStartDate("-7")

            CoroutineScope(ioDispatcher).launch {
                realm.write {
                    val reports = realm.query<ReportsEntity>("createdAt <= $0", date).find()

                    delete(reports)
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Unable to delete last seven days before data"
            )
        }
    }

    override suspend fun getDineOutOrders(
        startDate: String
    ): Flow<List<CartOrder>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val endDate = getCalculatedEndDate(date = startDate)

                    val cartOrders = realm.query<CartOrderEntity>(
                        "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                        OrderStatus.PROCESSING.name,
                        startDate,
                        endDate,
                        OrderType.DineOut.name
                    ).find().asFlow()

                    cartOrders.collectAndSend(
                        transform = { it.toExternalModel() },
                        send = { send(it) }
                    )
                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    private fun getProduct(productId: String): ProductEntity {
        return realm.query<ProductEntity>("productId == $0", productId).find().first()
    }

    private suspend fun getItemsReport(
        startDate: String,
        endDate: String
    ): Triple<Pair<Long, Long>, Pair<Long, Long>, Pair<Long, Long>> {
        return withContext(ioDispatcher) {
            try {
                //Get Today Expenses
                val expensesItem = async(ioDispatcher) {
                    realm.query<ExpensesEntity>(
                        "createdAt >= $0 AND createdAt <= $1",
                        startDate,
                        endDate
                    ).find()
                }

                //Get Today DineIn Orders
                val dineInItems = async(ioDispatcher) {
                    realm.query<CartOrderEntity>(
                        "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                        OrderStatus.PROCESSING.name,
                        startDate,
                        endDate,
                        OrderType.DineIn.name
                    ).find()
                }

                //Get DineOut Orders
                val dineOutItems = async(ioDispatcher) {
                    realm.query<CartOrderEntity>(
                        "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                        OrderStatus.PROCESSING.name,
                        startDate,
                        endDate,
                        OrderType.DineOut.name
                    ).find()
                }

                val totalExpensesItem = expensesItem.await().size.toLong()
                val totalExpensesAmount = expensesItem.await().sumOf {
                    it.expensesAmount.toLong()
                }

                val totalDineInItems = dineInItems.await().size.toLong()
                val totalDineInAmount = dineInItems.await().sumOf {
                    val price = countTotalPrice(it.cartOrderId)

                    price.first.minus(price.second)
                }.toLong()

                val totalDineOutItems = dineOutItems.await().size.toLong()
                val totalDineOutAmount = dineOutItems.await().sumOf {
                    val price = countTotalPrice(it.cartOrderId)

                    price.first.minus(price.second)
                }.toLong()

                Triple(
                    Pair(totalExpensesItem, totalExpensesAmount),
                    Pair(totalDineInItems, totalDineInAmount),
                    Pair(totalDineOutItems, totalDineOutAmount)
                )
            } catch (e: Exception) {
                Triple(Pair(0, 0), Pair(0, 0), Pair(0, 0))
            }
        }
    }

    private suspend fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
        return withContext(ioDispatcher) {
            var totalPrice = 0
            var discountPrice = 0

            val cartOrder = async(ioDispatcher) {
                realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()
            }.await()

            val cartOrders = async(ioDispatcher) {
                realm.query<CartEntity>("cartOrder.cartOrderId == $0", cartOrderId).find()
            }.await()

            if (cartOrder != null && cartOrders.isNotEmpty()) {
                if (cartOrder.doesChargesIncluded) {
                    val charges = realm.query<ChargesEntity>().find()
                    for (charge in charges) {
                        if (charge.isApplicable && cartOrder.orderType != OrderType.DineIn.name) {
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

            Pair(totalPrice, discountPrice)
        }
    }

}