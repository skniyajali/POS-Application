package com.niyaj.popos.features.reports.domain.model

import com.niyaj.popos.features.customer.domain.model.Customer

data class CustomerWiseReport(
    val customer: Customer? = null,
    val orderQty: Int = 0
)
