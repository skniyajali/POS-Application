package com.niyaj.popos.domain.util

sealed class UiEvent{
    data class IsLoading(val isLoading: Boolean? = false) : UiEvent()
    data class OnSuccess(val successMessage: String) : UiEvent()
    data class OnError(val errorMessage: String): UiEvent()
}
