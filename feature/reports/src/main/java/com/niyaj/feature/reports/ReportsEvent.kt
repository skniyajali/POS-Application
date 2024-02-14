package com.niyaj.feature.reports

import com.niyaj.model.OrderType

sealed class ReportsEvent {

    data class SelectDate(val date: String) : ReportsEvent()

    data class OnChangeOrderType(val orderType: OrderType? = null) : ReportsEvent()

    data class OnChangeCategoryOrderType(val orderType: OrderType? = null) : ReportsEvent()

    data class OnSelectCategory(val categoryName: String) : ReportsEvent()

    data object PrintReport : ReportsEvent()

    data object RefreshReport : ReportsEvent()

    data object GenerateReport : ReportsEvent()

}
