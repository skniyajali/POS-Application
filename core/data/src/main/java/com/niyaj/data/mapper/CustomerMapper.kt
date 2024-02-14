package com.niyaj.data.mapper

import com.niyaj.database.model.CustomerEntity
import com.niyaj.model.Customer

fun Customer.toEntity(): CustomerEntity {
    return CustomerEntity(
        customerId = this.customerId,
        customerPhone = this.customerPhone,
        customerName = this.customerName,
        customerEmail = customerEmail,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}