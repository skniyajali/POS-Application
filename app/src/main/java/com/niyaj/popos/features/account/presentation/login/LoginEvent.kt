package com.niyaj.popos.features.account.presentation.login

sealed interface LoginEvent {

    data class EmailOrPhoneChanged(val emailOrPhone: String): LoginEvent

    data class PasswordChanged(val password: String): LoginEvent

    object OnClickLogin: LoginEvent
}