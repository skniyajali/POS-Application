package com.niyaj.database.model

import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class PaymentEntity() : RealmObject {
    @PrimaryKey
    var paymentId: String = ""

    var employee: EmployeeEntity? = null

    var paymentAmount: String = ""

    var paymentDate: String = ""

    var paymentType: String = PaymentType.Salary.name

    var paymentMode: String = PaymentMode.Cash.name

    var paymentNote: String = ""

    var createdAt: String = ""

    var updatedAt: String? = null


    constructor(
        paymentId: String = "",
        employee: EmployeeEntity? = null,
        paymentMode: PaymentMode = PaymentMode.Cash,
        paymentAmount: String = "",
        paymentDate: String = "",
        paymentType: PaymentType = PaymentType.Salary,
        paymentNote: String = "",
        createdAt: String = "",
        updatedAt: String? = null
    ) : this() {
        this.paymentId = paymentId
        this.employee = employee
        this.paymentMode = paymentMode.name
        this.paymentAmount = paymentAmount
        this.paymentDate = paymentDate
        this.paymentType = paymentType.name
        this.paymentNote = paymentNote
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}

fun PaymentEntity.toExternalModel(): Payment {
    return Payment(
        paymentId = this.paymentId,
        employee = this.employee?.toExternalModel(),
        paymentMode = PaymentMode.valueOf(this.paymentMode),
        paymentAmount = this.paymentAmount,
        paymentDate = this.paymentDate,
        paymentType = PaymentType.valueOf(this.paymentType),
        paymentNote = this.paymentNote,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}