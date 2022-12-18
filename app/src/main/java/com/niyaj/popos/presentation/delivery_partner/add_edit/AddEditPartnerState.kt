package com.niyaj.popos.presentation.delivery_partner.add_edit

import com.niyaj.popos.domain.model.PartnerStatus
import com.niyaj.popos.domain.model.PartnerType

data class AddEditPartnerState(
    val partnerName: String = "",
    val partnerNameError: String? = null,
    val partnerEmail: String = "",
    val partnerEmailError: String? = null,
    val partnerPhone: String = "",
    val partnerPhoneError: String? = null,
    val partnerPassword: String = "",
    val partnerPasswordError: String? = null,
    val partnerStatus: String = PartnerStatus.InActive.partnerStatus,
    val partnerType: String  = PartnerType.FullTime.partnerType,
)
