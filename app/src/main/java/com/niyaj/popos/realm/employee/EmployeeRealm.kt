package com.niyaj.popos.realm.employee

import com.niyaj.popos.util.Constants
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class EmployeeRealm(): RealmObject {

    @PrimaryKey
    var _id: String = ObjectId().toHexString()

    var employeeName: String = ""

    var employeePhone: String = ""

    var employeeSalary: String = ""

    var employeeSalaryType: String = ""

    var employeeType: String = ""

    var employeePosition: String = ""

    var employeeJoinedDate: String = ""

    var created_at: String? = System.currentTimeMillis().toString()

    var updated_at: String? = null

    var isGlobalAdmin: Boolean = true

    var _partition: String = Constants.REALM_PARTITION_NAME

    var owner_id: String = ""

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}