package com.niyaj.popos.realm.reports

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class ReportsRealm: RealmObject {

    @PrimaryKey
    var _id: String = BsonObjectId().toHexString()

    var expensesQty: Long = 0
    var expensesAmount: Long = 0

    var dineInSalesQty: Long = 0
    var dineInSalesAmount: Long = 0

    var dineOutSalesQty: Long = 0
    var dineOutSalesAmount: Long = 0

    var reportDate: String = ""

    var createdAt: String = System.currentTimeMillis().toString()

    var updatedAt: String? = null

}