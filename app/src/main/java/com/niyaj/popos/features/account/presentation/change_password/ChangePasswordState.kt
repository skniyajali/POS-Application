package com.niyaj.popos.features.account.presentation.change_password

data class ChangePasswordState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
)
