package com.niyaj.data.mapper

import com.niyaj.model.Customer
import com.niyaj.model.ImportContact
import org.mongodb.kbson.BsonObjectId

fun ImportContact.toCustomer(): Customer? {
    try {
        val phoneNo =
            if (!mobilePhone.isNullOrEmpty() && mobilePhone!!.length >= 10) mobilePhone!!.takeLast(
                10
            ) else null

        if (phoneNo != null) {
            return Customer(
                customerId = BsonObjectId().toHexString(),
                customerPhone = phoneNo,
                customerName = displayName ?: "",
                customerEmail = eMailAddress ?: ""
            )
        }

        return null
    } catch (e: Exception) {
        return null
    }
}

fun List<ImportContact>.toCustomers(): List<Customer> {
    return this.map { contact ->
        contact.toCustomer() ?: Customer()
    }
}