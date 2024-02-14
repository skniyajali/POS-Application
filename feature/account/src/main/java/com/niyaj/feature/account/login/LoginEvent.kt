package com.niyaj.feature.account.login

sealed interface LoginEvent {

    data class EmailOrPhoneChanged(val emailOrPhone: String) : LoginEvent

    data class PasswordChanged(val password: String) : LoginEvent

    data object OnClickLogin : LoginEvent
}