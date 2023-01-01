package com.niyaj.popos.features.reports.domain.model

import com.niyaj.popos.features.address.domain.model.Address

data class AddressWiseReport(
    val address: Address? = null,
    val orderQty: Int = 0,
)
