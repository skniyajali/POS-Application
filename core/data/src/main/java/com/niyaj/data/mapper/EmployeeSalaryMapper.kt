package com.niyaj.data.mapper

import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.PaymentEntity
import com.niyaj.model.Payment
import org.mongodb.kbson.BsonObjectId

fun Payment.toEntity(): PaymentEntity {
    return PaymentEntity(
        paymentId = this.paymentId.ifEmpty { BsonObjectId().toHexString() },
        employee = this.employee?.toEntity(),
        paymentMode = this.paymentMode,
        paymentAmount = this.paymentAmount,
        paymentDate = this.paymentDate,
        paymentType = this.paymentType,
        paymentNote = this.paymentNote,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Payment.toEntity(employeeEntity: EmployeeEntity?): PaymentEntity {
    return PaymentEntity(
        paymentId = this.paymentId.ifEmpty { BsonObjectId().toHexString() },
        employee = employeeEntity,
        paymentMode = this.paymentMode,
        paymentAmount = this.paymentAmount,
        paymentDate = this.paymentDate,
        paymentType = this.paymentType,
        paymentNote = this.paymentNote,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}