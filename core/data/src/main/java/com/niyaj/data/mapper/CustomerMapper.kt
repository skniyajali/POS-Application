package com.niyaj.data.mapper

import com.niyaj.database.model.CustomerEntity
import com.niyaj.model.Customer
import org.mongodb.kbson.BsonObjectId

fun Customer.toEntity(): CustomerEntity {
    return CustomerEntity(
        customerId = this.customerId.ifEmpty {  BsonObjectId().toHexString() },
        customerPhone = this.customerPhone,
        customerName = this.customerName,
        customerEmail = customerEmail,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}