package com.niyaj.popos.features.delivery_partner.domain.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.delivery_partner.domain.model.DeliveryPartner
import kotlinx.coroutines.flow.Flow

interface PartnerRepository {

    suspend fun getAllPartner(): Flow<Resource<List<DeliveryPartner>>>

    suspend fun getPartnerById(partnerId: String): Resource<DeliveryPartner?>

    fun getPartnerByEmail(partnerEmail: String, partnerId: String? = null): Boolean

    fun getPartnerByPhone(partnerPhone: String, partnerId: String? = null): Boolean

    suspend fun createNewPartner(newPartner: DeliveryPartner): Resource<Boolean>

    suspend fun updatePartner(newPartner: DeliveryPartner, partnerId: String): Resource<Boolean>

    suspend fun deletePartner(partnerId: String): Resource<Boolean>
}