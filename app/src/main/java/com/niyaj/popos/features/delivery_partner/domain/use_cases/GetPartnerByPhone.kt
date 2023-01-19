package com.niyaj.popos.features.delivery_partner.domain.use_cases

import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerRepository

class GetPartnerByPhone(
    private val partnerRepository: PartnerRepository
) {
    operator fun invoke(partnerPhone: String, partnerId: String? = null): Boolean {
        return partnerRepository.getPartnerByPhone(partnerPhone, partnerId)
    }
}