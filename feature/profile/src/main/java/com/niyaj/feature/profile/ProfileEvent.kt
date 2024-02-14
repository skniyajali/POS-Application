package com.niyaj.feature.profile

sealed class ProfileEvent {

    data class NameChanged(val name: String) : ProfileEvent()

    data class TaglineChanged(val tagline: String) : ProfileEvent()

    data class DescriptionChanged(val description: String) : ProfileEvent()

    data class EmailChanged(val email: String) : ProfileEvent()

    data class PrimaryPhoneChanged(val primaryPhone: String) : ProfileEvent()

    data class SecondaryPhoneChanged(val secondaryPhone: String) : ProfileEvent()

    data class AddressChanged(val address: String) : ProfileEvent()

    data class PaymentQrCodeChanged(val paymentQrCode: String) : ProfileEvent()

    data object StartScanning : ProfileEvent()

    data object LogoChanged : ProfileEvent()

    data object PrintLogoChanged : ProfileEvent()

    data object SetProfileInfo: ProfileEvent()

    data object UpdateProfile : ProfileEvent()

    data object LogoutProfile: ProfileEvent()
}
