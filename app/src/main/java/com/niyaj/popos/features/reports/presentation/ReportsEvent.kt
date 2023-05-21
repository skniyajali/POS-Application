package com.niyaj.popos.features.reports.presentation

sealed class ReportsEvent {

    data class SelectDate(val date: String) : ReportsEvent()

    data class OnChangeOrderType(val orderType: String = "") : ReportsEvent()

    data class OnChangeCategoryOrderType(val orderType: String = "") : ReportsEvent()

    data class OnSelectCategory(val categoryName: String) : ReportsEvent()

    object PrintReport : ReportsEvent()

    object RefreshReport : ReportsEvent()

    object GenerateReport : ReportsEvent()

}
