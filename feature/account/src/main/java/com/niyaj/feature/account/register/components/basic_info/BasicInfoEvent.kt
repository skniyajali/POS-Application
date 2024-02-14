package com.niyaj.feature.account.register.components.basic_info

sealed interface BasicInfoEvent {
    data class TaglineChanged(val tagline : String) : BasicInfoEvent
    data class AddressChanged(val address : String) : BasicInfoEvent
    data class DescriptionChanged(val description : String) : BasicInfoEvent
    data class PaymentQRChanged(val paymentQrCode : String) : BasicInfoEvent
    data class PrintLogoChanged(val printLogo : String) : BasicInfoEvent
    data object StartScanning : BasicInfoEvent
}
