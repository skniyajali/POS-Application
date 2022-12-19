package com.niyaj.popos.realm.delivery_partner.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.delivery_partner.domain.repository.PartnerRepository

class GetPartnerByEmail(
    private val partnerRepository: PartnerRepository
) {

    suspend operator fun invoke(partnerEmail: String, partnerId: String? = null): Resource<Boolean>{
        return partnerRepository.getPartnerByEmail(partnerEmail, partnerId)
    }

}