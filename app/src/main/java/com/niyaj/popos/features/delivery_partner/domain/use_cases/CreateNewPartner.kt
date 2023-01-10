package com.niyaj.popos.features.delivery_partner.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerRepository

class CreateNewPartner(
    private val partnerRepository: PartnerRepository
) {
    suspend operator fun invoke(newDeliveryPartner: DeliveryPartner): Resource<Boolean> {
        return partnerRepository.createNewPartner(newDeliveryPartner)
    }
}