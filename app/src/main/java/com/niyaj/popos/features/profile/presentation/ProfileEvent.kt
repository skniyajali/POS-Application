package com.niyaj.popos.features.profile.presentation

sealed class ProfileEvent {

    data class NameChanged(val name: String) : ProfileEvent()

    data class TaglineChanged(val tagline: String) : ProfileEvent()

    data class DescriptionChanged(val description: String) : ProfileEvent()

    data class EmailChanged(val email: String) : ProfileEvent()

    data class PrimaryPhoneChanged(val primaryPhone: String) : ProfileEvent()

    data class SecondaryPhoneChanged(val secondaryPhone: String) : ProfileEvent()

    data class AddressChanged(val address: String) : ProfileEvent()

    data class PaymentQrCodeChanged(val paymentQrCode: String) : ProfileEvent()

    object StartScanning : ProfileEvent()

    object LogoChanged : ProfileEvent()

    object PrintLogoChanged : ProfileEvent()

    object RefreshEvent : ProfileEvent()

    object SetProfileInfo: ProfileEvent()

    object UpdateProfile : ProfileEvent()
}
