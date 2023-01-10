package com.niyaj.popos.features.delivery_partner.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerRepository

class GetPartnerByPhone(
    private val partnerRepository: PartnerRepository
) {
    suspend operator fun invoke(partnerPhone: String, partnerId: String? = null): Resource<Boolean> {
        return partnerRepository.getPartnerByPhone(partnerPhone, partnerId)
    }
}