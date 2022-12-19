package com.niyaj.popos.realm.delivery_partner.domain.use_cases

data class PartnerUseCases(
    val getAllPartners: GetAllPartners,
    val getPartnerById: GetPartnerById,
    val getPartnerByEmail: GetPartnerByEmail,
    val getPartnerByPhone: GetPartnerByPhone,
    val createNewPartner: CreateNewPartner,
    val updatePartner: UpdatePartner,
    val deletePartner: DeletePartner,
)
