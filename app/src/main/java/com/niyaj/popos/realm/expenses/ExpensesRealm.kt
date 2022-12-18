package com.niyaj.popos.realm.expenses

import com.niyaj.popos.realm.expenses_category.ExpensesCategoryRealm
import com.niyaj.popos.util.Constants
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class ExpensesRealm(): RealmObject {
    @PrimaryKey
    var _id: String = ObjectId().toHexString()

    var expansesCategory: ExpensesCategoryRealm? = null

    var expansesPrice: String = ""

    var expansesRemarks: String = ""

    var created_at: String? = System.currentTimeMillis().toString()

    var updated_at: String? = null

    var isGlobalAdmin: Boolean = true

    var _partition: String = Constants.REALM_PARTITION_NAME

    var owner_id: String = ""

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}