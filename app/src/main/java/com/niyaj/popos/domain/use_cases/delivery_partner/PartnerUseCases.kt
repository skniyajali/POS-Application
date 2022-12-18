package com.niyaj.popos.domain.use_cases.delivery_partner

data class PartnerUseCases(
    val getAllPartners: GetAllPartners,
    val getPartnerById: GetPartnerById,
    val getPartnerByEmail: GetPartnerByEmail,
    val getPartnerByPhone: GetPartnerByPhone,
    val createNewPartner: CreateNewPartner,
    val updatePartner: UpdatePartner,
    val deletePartner: DeletePartner,
)
