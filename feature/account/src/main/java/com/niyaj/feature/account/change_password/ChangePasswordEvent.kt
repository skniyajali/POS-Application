package com.niyaj.feature.account.change_password

sealed interface ChangePasswordEvent {

    data class CurrentPasswordChanged(val currentPassword: String) : ChangePasswordEvent

    data class NewPasswordChanged(val newPassword: String) : ChangePasswordEvent

    data class ConfirmPasswordChanged(val confirmPassword: String) : ChangePasswordEvent

    data object ChangePassword : ChangePasswordEvent
}