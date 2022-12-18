package com.niyaj.popos.presentation.report

sealed class ReportsEvent{
    data class SelectDate(val date: String): ReportsEvent()

    data class OnChangeOrderType(val orderType: String = ""): ReportsEvent()

    object PrintReport: ReportsEvent()

    object RefreshReport: ReportsEvent()

}
