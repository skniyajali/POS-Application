package com.niyaj.data.mapper

import com.niyaj.database.model.ReportsEntity
import com.niyaj.model.Reports

fun Reports.toEntity(): ReportsEntity {
    return ReportsEntity(
        reportId = reportId,
        expensesQty = expensesQty,
        expensesAmount = expensesAmount,
        dineInSalesQty = dineInSalesQty,
        dineInSalesAmount = dineInSalesAmount,
        dineOutSalesQty = dineOutSalesQty,
        dineOutSalesAmount = dineOutSalesAmount,
        reportDate = reportDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}