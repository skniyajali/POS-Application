package com.niyaj.popos.realm.expenses

import com.niyaj.popos.realm.expenses_category.ExpensesCategoryRealm
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
}