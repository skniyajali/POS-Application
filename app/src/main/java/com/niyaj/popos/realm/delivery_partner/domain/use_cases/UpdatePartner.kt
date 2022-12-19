package com.niyaj.popos.realm.delivery_partner.domain.use_cases

import com.niyaj.popos.realm.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.realm.delivery_partner.domain.repository.PartnerRepository
import com.niyaj.popos.domain.util.Resource

class UpdatePartner(
    private val partnerRepository: PartnerRepository
) {

    suspend operator fun invoke(newDeliveryPartner: DeliveryPartner, partnerId: String): Resource<Boolean>{
        return partnerRepository.updatePartner(newDeliveryPartner, partnerId)
    }
}