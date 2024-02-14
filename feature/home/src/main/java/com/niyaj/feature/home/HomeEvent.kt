package com.niyaj.feature.home

sealed class HomeEvent {
    data object GetSelectedOrder: HomeEvent()

    data object RefreshHome: HomeEvent()
}
