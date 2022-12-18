package com.niyaj.popos.realm.employee_attendance

import com.niyaj.popos.realm.employee.EmployeeRealm
import com.niyaj.popos.util.Constants
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class AttendanceRealm(): RealmObject {
    @PrimaryKey
    var _id: String = BsonObjectId().toHexString()

    var employee: EmployeeRealm? = null

    var isAbsent: Boolean = false

    var absentReason: String = ""

    var absentDate: String = ""

    var created_at: String = System.currentTimeMillis().toString()

    var updated_at: String? = null

    var isGlobalAdmin: Boolean = true

    var _partition: String = Constants.REALM_PARTITION_NAME

    var owner_id: String = ""

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }

}