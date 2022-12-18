package com.niyaj.popos.realm.employee_salary

import com.niyaj.popos.realm.employee.EmployeeRealm
import com.niyaj.popos.util.Constants
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class SalaryRealm(): RealmObject {
    @PrimaryKey
    var _id: String = BsonObjectId().toHexString()

    var employee: EmployeeRealm? = null

    var salaryType: String = ""

    var employeeSalary: String = ""

    var salaryGivenDate: String = ""

    var salaryPaymentType: String = ""

    var salaryNote: String = ""

    var created_at: String = System.currentTimeMillis().toString()

    var updated_at: String? = null

    var isGlobalAdmin: Boolean = true

    var _partition: String = Constants.REALM_PARTITION_NAME

    var owner_id: String = ""

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}