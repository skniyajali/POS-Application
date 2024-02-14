package com.niyaj.feature.account.login

data class LoginState(
    val emailOrPhone: String = "",
    val emailError: String? = null,

    val password: String = "",
    val passwordError: String? = null,
)
