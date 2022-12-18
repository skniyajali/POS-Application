package com.niyaj.popos.realm.employee

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
}