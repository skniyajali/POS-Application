package com.niyaj.popos.presentation.delivery_partner.add_edit

sealed class AddEditPartnerEvent {

    data class PartnerNameChanged(val partnerName: String) : AddEditPartnerEvent()

    data class PartnerPhoneChanged(val partnerPhone: String) : AddEditPartnerEvent()

    data class PartnerEmailChanged(val partnerEmail: String) : AddEditPartnerEvent()

    data class PartnerPasswordChanged(val partnerPassword: String) : AddEditPartnerEvent()

    data class PartnerStatusChanged(val partnerStatus: String) : AddEditPartnerEvent()

    data class PartnerTypeChanged(val partnerType: String) : AddEditPartnerEvent()

    object CreateNewPartner : AddEditPartnerEvent()

    data class UpdatePartner(val partnerId: String) : AddEditPartnerEvent()
}
