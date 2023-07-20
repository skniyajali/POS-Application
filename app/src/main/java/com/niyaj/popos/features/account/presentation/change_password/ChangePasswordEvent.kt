package com.niyaj.popos.features.account.presentation.change_password

sealed interface ChangePasswordEvent {

    data class CurrentPasswordChanged(val currentPassword: String) : ChangePasswordEvent

    data class NewPasswordChanged(val newPassword: String) : ChangePasswordEvent

    data class ConfirmPasswordChanged(val confirmPassword: String) : ChangePasswordEvent

    object ChangePassword: ChangePasswordEvent
}