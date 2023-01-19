package com.niyaj.popos.features.delivery_partner.domain.use_cases

import com.niyaj.popos.features.delivery_partner.domain.use_cases.validation.ValidatePartnerEmail
import com.niyaj.popos.features.delivery_partner.domain.use_cases.validation.ValidatePartnerName
import com.niyaj.popos.features.delivery_partner.domain.use_cases.validation.ValidatePartnerPassword
import com.niyaj.popos.features.delivery_partner.domain.use_cases.validation.ValidatePartnerPhone

data class PartnerUseCases(
    val validatePartnerName: ValidatePartnerName,
    val validatePartnerEmail: ValidatePartnerEmail,
    val validatePartnerPhone: ValidatePartnerPhone,
    val validatePartnerPassword: ValidatePartnerPassword,
    val getAllPartners: GetAllPartners,
    val getPartnerById: GetPartnerById,
    val getPartnerByEmail: GetPartnerByEmail,
    val getPartnerByPhone: GetPartnerByPhone,
    val createNewPartner: CreateNewPartner,
    val updatePartner: UpdatePartner,
    val deletePartner: DeletePartner,
)
