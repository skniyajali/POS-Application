package com.niyaj.popos.domain.use_cases.delivery_partner

import com.niyaj.popos.domain.model.DeliveryPartner
import com.niyaj.popos.domain.repository.PartnerRepository
import com.niyaj.popos.domain.util.Resource

class UpdatePartner(
    private val partnerRepository: PartnerRepository
) {

    suspend operator fun invoke(newDeliveryPartner: DeliveryPartner, partnerId: String): Resource<Boolean>{
        return partnerRepository.updatePartner(newDeliveryPartner, partnerId)
    }
}