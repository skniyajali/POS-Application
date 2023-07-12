package com.niyaj.popos.features.common.util

sealed class UiEvent{
    data class IsLoading(val isLoading: Boolean? = false) : UiEvent()
    data class Success(val successMessage: String) : UiEvent()
    data class Error(val errorMessage: String): UiEvent()
}
