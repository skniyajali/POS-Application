package com.niyaj.popos.domain.use_cases.delivery_partner

import com.niyaj.popos.domain.repository.PartnerRepository
import com.niyaj.popos.domain.util.Resource

class DeletePartner(
    private val partnerRepository: PartnerRepository
) {

    suspend operator fun invoke(partnerId: String): Resource<Boolean>{
        return partnerRepository.deletePartner(partnerId)
    }
}