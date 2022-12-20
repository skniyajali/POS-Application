package com.niyaj.popos.features.delivery_partner.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerRepository

class DeletePartner(
    private val partnerRepository: PartnerRepository
) {

    suspend operator fun invoke(partnerId: String): Resource<Boolean> {
        return partnerRepository.deletePartner(partnerId)
    }
}