package com.niyaj.popos.features.profile.presentation

data class UpdateProfileState(
    val name: String = "",
    val nameError: String? = null,

    val tagline: String = "",
    val taglineError: String? = null,

    val email: String = "",
    val emailError: String? = null,

    val primaryPhone: String = "",
    val primaryPhoneError: String? = null,

    val secondaryPhone: String = "",
    val secondaryPhoneError: String? = null,

    val address: String = "",
    val addressError: String? = null,

    val paymentQrCode: String = "",
    val paymentQrCodeError: String? = null,

    val description: String = "",
)
