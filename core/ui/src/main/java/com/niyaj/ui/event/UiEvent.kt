package com.niyaj.ui.event

sealed class UiEvent {
    data class Success(val successMessage: String) : UiEvent()

    data class Error(val errorMessage: String) : UiEvent()
}
