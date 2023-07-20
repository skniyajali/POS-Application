package com.niyaj.popos.features.account.presentation.register.components.login_info

sealed interface LoginInfoEvent {
    data class NameChanged(val name : String) : LoginInfoEvent
    data class EmailChanged(val email : String) : LoginInfoEvent
    data class SecondaryPhoneChanged(val secondaryPhone : String) : LoginInfoEvent
    data class PhoneChanged(val phone : String) : LoginInfoEvent
    data class PasswordChanged(val password : String) : LoginInfoEvent
    data class LogoChanged(val logo : String) : LoginInfoEvent
}
