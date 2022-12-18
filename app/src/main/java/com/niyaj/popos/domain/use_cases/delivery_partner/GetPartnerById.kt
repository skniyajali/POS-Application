package com.niyaj.popos.domain.use_cases.delivery_partner

import com.niyaj.popos.domain.model.DeliveryPartner
import com.niyaj.popos.domain.repository.PartnerRepository
import com.niyaj.popos.domain.util.Resource

class GetPartnerById(
    private val partnerRepository: PartnerRepository
) {

    suspend operator fun invoke(partnerId: String): Resource<DeliveryPartner?>{
        return partnerRepository.getPartnerById(partnerId)
    }
}