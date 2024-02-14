package com.niyaj.model

data class TotalOrderDetails(
    val totalAmount: String = "0",
    val totalOrder: Int = 0,
    val repeatedCustomer: Int = 0,
    val datePeriod: Pair<String, String> = Pair("", "")
)