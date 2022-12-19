package com.niyaj.popos.realm.employee_attendance

import com.niyaj.popos.realm.employee.domain.model.Employee
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class AttendanceRealm(): RealmObject {
    @PrimaryKey
    var _id: String = BsonObjectId().toHexString()

    var employee: Employee? = null

    var isAbsent: Boolean = false

    var absentReason: String = ""

    var absentDate: String = ""

    var created_at: String = System.currentTimeMillis().toString()

    var updated_at: String? = null

}