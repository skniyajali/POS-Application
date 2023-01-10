package com.niyaj.popos.features.delivery_partner.data.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.isValid
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class PartnerRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PartnerRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Delivery Partner Session:")
    }


    override suspend fun getAllPartner(): Flow<Resource<List<DeliveryPartner>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val partners = realm.query<DeliveryPartner>().sort("partnerId", Sort.DESCENDING).find()

                    val items = partners.asFlow()
                    items.collect { changes: ResultsChange<DeliveryPartner> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }

                            is InitialResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                } catch (e: Exception) {
                    send(Resource.Loading(false))
                    send(Resource.Error(e.message ?: "Unable to get delivery partners", emptyList()))
                }
            }
        }
    }

    override suspend fun getPartnerById(partnerId: String): Resource<DeliveryPartner?> {
        return try {
            val partners = withContext(ioDispatcher) {
                realm.query<DeliveryPartner>("partnerId == $0", partnerId).first().find()
            }

            Resource.Success(partners)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Partner", null)
        }
    }

    override suspend fun getPartnerByEmail(
        partnerEmail: String,
        partnerId: String?,
    ): Resource<Boolean> {

        val findPartnerByEmail: DeliveryPartner? = if (partnerId.isNullOrEmpty()) {
            realm.query<DeliveryPartner>("partnerEmail == $0", partnerEmail).first().find()
        } else {
            realm.query<DeliveryPartner>(
                "partnerEmail == $0 AND partnerId != $1",
                partnerEmail,
                partnerId
            ).first().find()
        }

        return if (findPartnerByEmail != null) {
            Resource.Error("Partner email already exists", false)
        } else {
            Resource.Success(true)
        }

    }

    override suspend fun getPartnerByPhone(
        partnerPhone: String,
        partnerId: String?,
    ): Resource<Boolean> {

        val findPartnerByPhone: DeliveryPartner? = if (partnerId.isNullOrEmpty()) {
            realm.query<DeliveryPartner>("partnerPhone == $0", partnerPhone).first().find()
        } else {
            realm.query<DeliveryPartner>(
                "partnerPhone == $0 AND partnerId != $1",
                partnerPhone,
                partnerId
            )
                .first().find()
        }

        return if (findPartnerByPhone != null) {
            Resource.Error("Partner phone already exists", false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun createNewPartner(newPartner: DeliveryPartner): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val findPartnerByEmail =
                    realm.query<DeliveryPartner>("partnerEmail == $0", newPartner.partnerEmail)
                        .first().find()
                val findPartnerByPhone =
                    realm.query<DeliveryPartner>("partnerPhone == $0", newPartner.partnerPhone)
                        .first().find()

                if (findPartnerByEmail != null) {
                    Resource.Error("Partner email already exists", false)
                } else if (findPartnerByPhone != null) {
                    Resource.Error("Partner phone already exists", false)
                } else {
                    val partner = DeliveryPartner()
                    partner.partnerId = BsonObjectId().toHexString()
                    partner.partnerName = newPartner.partnerName
                    partner.partnerEmail = newPartner.partnerEmail
                    partner.partnerPhone = newPartner.partnerPhone
                    partner.partnerPassword = newPartner.partnerPassword
                    partner.partnerStatus = newPartner.partnerStatus
                    partner.partnerType = newPartner.partnerType
                    partner.createdAt = System.currentTimeMillis().toString()

                    val result = realm.write {
                        this.copyToRealm(partner)
                    }

                    Resource.Success(result.isValid())
                }
            }
        } catch (e: RealmException) {
            Resource.Error(e.message ?: "Error creating Delivery Partner", true)
        }
    }

    override suspend fun updatePartner(
        newPartner: DeliveryPartner,
        partnerId: String,
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val findPartnerByEmail = realm.query<DeliveryPartner>(
                    "partnerEmail == $0 AND partnerId != $1",
                    newPartner.partnerEmail,
                    partnerId
                ).first().find()
                val findPartnerByPhone = realm.query<DeliveryPartner>(
                    "partnerPhone == $0 AND partnerId != $1",
                    newPartner.partnerPhone,
                    partnerId
                ).first().find()

                if (findPartnerByEmail != null) {
                    Resource.Error("Partner email already exists", false)
                } else if (findPartnerByPhone != null) {
                    Resource.Error("Partner phone already exists", false)
                } else {
                    realm.write {
                        val partner =
                            this.query<DeliveryPartner>("partnerId == $0", partnerId).first().find()
                        partner?.partnerName = newPartner.partnerName
                        partner?.partnerEmail = newPartner.partnerEmail
                        partner?.partnerPhone = newPartner.partnerPhone
                        partner?.partnerPassword = newPartner.partnerPassword
                        partner?.partnerStatus = newPartner.partnerStatus
                        partner?.partnerType = newPartner.partnerType
                        partner?.updatedAt = System.currentTimeMillis().toString()
                    }

                    Resource.Success(true)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update delivery partner.", false)
        }
    }

    override suspend fun deletePartner(partnerId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                realm.write {
                    val partner: DeliveryPartner =
                        this.query<DeliveryPartner>("partnerId == $0", partnerId).find().first()

                    delete(partner)
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete delivery partner", false)
        }
    }
}