package com.niyaj.popos.features.account.presentation.login

data class LoginState(
    val emailOrPhone: String = "",
    val emailError: String? = null,

    val password: String = "",
    val passwordError: String? = null,
)
