package com.niyaj.data.mapper

import com.niyaj.database.model.PaymentEntity
import com.niyaj.model.Payment

fun Payment.toEntity(): PaymentEntity {
    return PaymentEntity(
        paymentId = this.paymentId,
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