package com.niyaj.popos.domain.use_cases.delivery_partner

import com.niyaj.popos.domain.repository.PartnerRepository
import com.niyaj.popos.domain.util.Resource

class GetPartnerByEmail(
    private val partnerRepository: PartnerRepository
) {

    suspend operator fun invoke(partnerEmail: String, partnerId: String? = null): Resource<Boolean>{
        return partnerRepository.getPartnerByEmail(partnerEmail, partnerId)
    }

}