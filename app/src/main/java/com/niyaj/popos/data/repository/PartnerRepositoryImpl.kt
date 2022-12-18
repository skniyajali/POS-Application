package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.DeliveryPartner
import com.niyaj.popos.domain.repository.PartnerRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.delivery_partner.PartnerRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PartnerRepositoryImpl(
    private val partnerRealmDao: PartnerRealmDao
) : PartnerRepository {
    override suspend fun getAllPartner(): Flow<Resource<List<DeliveryPartner>>> {
        return flow {
            partnerRealmDao.getAllPartner().collect{ result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.map { partner ->
                                DeliveryPartner(
                                    deliveryPartnerId = partner._id,
                                    deliveryPartnerName = partner.partnerName,
                                    deliveryPartnerEmail = partner.partnerEmail,
                                    deliveryPartnerPhone = partner.partnerPhone,
                                    deliveryPartnerPassword = partner.partnerPassword,
                                    deliveryPartnerStatus = partner.partnerStatus,
                                    deliveryPartnerType = partner.partnerType,
                                    createdAt = partner.created_at,
                                    updatedAt = partner.updated_at
                                )
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get delivery partners from database"))
                    }
                }
            }
        }
    }

    override suspend fun getPartnerById(partnerId: String): Resource<DeliveryPartner?> {
        val result = partnerRealmDao.getPartnerById(partnerId)

        return result.data?.let { partner ->
            Resource.Success(
                data = DeliveryPartner(
                    deliveryPartnerId = partner._id,
                    deliveryPartnerName = partner.partnerName,
                    deliveryPartnerEmail = partner.partnerEmail,
                    deliveryPartnerPhone = partner.partnerPhone,
                    deliveryPartnerPassword = partner.partnerPassword,
                    deliveryPartnerStatus = partner.partnerStatus,
                    deliveryPartnerType = partner.partnerType,
                    createdAt = partner.created_at,
                    updatedAt = partner.updated_at
                )
            )
        } ?: Resource.Error(result.message ?: "Could not get charges item")
    }

    override suspend fun getPartnerByEmail(
        partnerEmail: String,
        partnerId: String?,
    ): Resource<Boolean> {
        return partnerRealmDao.getPartnerByEmail(partnerEmail, partnerId)
    }

    override suspend fun getPartnerByPhone(
        partnerPhone: String,
        partnerId: String?,
    ): Resource<Boolean> {
        return partnerRealmDao.getPartnerByPhone(partnerPhone, partnerId)
    }

    override suspend fun createNewPartner(newPartner: DeliveryPartner): Resource<Boolean> {
        return partnerRealmDao.createNewPartner(newPartner)
    }

    override suspend fun updatePartner(
        newPartner: DeliveryPartner,
        partnerId: String,
    ): Resource<Boolean> {
        return partnerRealmDao.updatePartner(newPartner, partnerId)
    }

    override suspend fun deletePartner(partnerId: String): Resource<Boolean> {
        return partnerRealmDao.deletePartner(partnerId)
    }
}