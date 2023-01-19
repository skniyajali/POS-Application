package com.niyaj.popos.features.delivery_partner.domain.use_cases

import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerRepository

class GetPartnerByEmail(
    private val partnerRepository: PartnerRepository
) {
    suspend operator fun invoke(partnerEmail: String, partnerId: String? = null): Boolean {
        return partnerRepository.getPartnerByEmail(partnerEmail, partnerId)
    }
}