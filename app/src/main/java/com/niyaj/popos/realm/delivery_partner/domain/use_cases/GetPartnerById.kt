package com.niyaj.popos.realm.delivery_partner.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.realm.delivery_partner.domain.repository.PartnerRepository

class GetPartnerById(
    private val partnerRepository: PartnerRepository
) {

    suspend operator fun invoke(partnerId: String): Resource<DeliveryPartner?>{
        return partnerRepository.getPartnerById(partnerId)
    }
}