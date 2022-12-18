package com.niyaj.popos.domain.use_cases.delivery_partner

import com.niyaj.popos.domain.repository.PartnerRepository
import com.niyaj.popos.domain.util.Resource

class GetPartnerByPhone(
    private val partnerRepository: PartnerRepository
) {

    suspend operator fun invoke(partnerPhone: String, partnerId: String? = null): Resource<Boolean> {
        return partnerRepository.getPartnerByPhone(partnerPhone, partnerId)
    }
}