package com.niyaj.popos.realm.delivery_partner

import com.niyaj.popos.domain.model.DeliveryPartner
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realmApp
import io.realm.kotlin.Realm
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.isValid
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

class PartnerRealmDaoImpl(config: SyncConfiguration) : PartnerRealmDao {

    private val user = realmApp.currentUser


    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        if(user == null && sessionState != "ACTIVE") {
            Timber.d("Delivery Partner: user is null")
        }

        Timber.d("Delivery Partner Session: $sessionState")


        CoroutineScope(Dispatchers.IO).launch {
            realm.syncSession.uploadAllLocalChanges()
            realm.syncSession.downloadAllServerChanges()
            realm.subscriptions.waitForSynchronization()
        }
    }


    override suspend fun getAllPartner(): Flow<Resource<List<PartnerRealm>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val items = realm.query<PartnerRealm>().sort("_id", Sort.DESCENDING).find().asFlow()

                items.collect { changes: ResultsChange<PartnerRealm> ->
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
            }catch (e: Exception){
                send(Resource.Error(e.message ?: "Unable to get delivery partners", null))
            }
        }
    }

    override suspend fun getPartnerById(partnerId: String): Resource<PartnerRealm?> {
        return try {
            val partners = realm.query<PartnerRealm>("_id == $0", partnerId).first().find()

            Resource.Success(partners)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Partner", null)
        }
    }

    override suspend fun getPartnerByEmail(
        partnerEmail: String,
        partnerId: String?,
    ): Resource<Boolean> {

        val findPartnerByEmail: PartnerRealm? = if(partnerId.isNullOrEmpty()) {
            realm.query<PartnerRealm>("partnerEmail == $0", partnerEmail).first().find()
        }else {
            realm.query<PartnerRealm>("partnerEmail == $0 AND _id != $1", partnerEmail, partnerId).first().find()
        }

        return if(findPartnerByEmail != null){
            Resource.Error("Partner email already exists", false)
        }else {
            Resource.Success(true)
        }

    }

    override suspend fun getPartnerByPhone(
        partnerPhone: String,
        partnerId: String?,
    ): Resource<Boolean> {

        val findPartnerByPhone: PartnerRealm? = if(partnerId.isNullOrEmpty()) {
            realm.query<PartnerRealm>("partnerPhone == $0", partnerPhone).first().find()
        }else {
            realm.query<PartnerRealm>("partnerPhone == $0 AND _id != $1", partnerPhone, partnerId).first().find()
        }

        return if(findPartnerByPhone != null){
            Resource.Error("Partner phone already exists", false)
        }else {
            Resource.Success(true)
        }
    }

    override suspend fun createNewPartner(newPartner: DeliveryPartner): Resource<Boolean> {
        if (user != null){
            return try {

                val findPartnerByEmail = realm.query<PartnerRealm>("partnerEmail == $0", newPartner.deliveryPartnerEmail).first().find()
                val findPartnerByPhone = realm.query<PartnerRealm>("partnerPhone == $0", newPartner.deliveryPartnerPhone).first().find()

                if(findPartnerByEmail != null){
                    Resource.Error("Partner email already exists", false)
                }else if(findPartnerByPhone != null){
                    Resource.Error("Partner phone already exists", false)
                }else{
                    val partner = PartnerRealm(user.id)
                    partner.partnerName = newPartner.deliveryPartnerName
                    partner.partnerEmail = newPartner.deliveryPartnerEmail
                    partner.partnerPhone = newPartner.deliveryPartnerPhone
                    partner.partnerPassword = newPartner.deliveryPartnerPassword
                    partner.partnerStatus = newPartner.deliveryPartnerStatus
                    partner.partnerType = newPartner.deliveryPartnerType

                    val result = realm.write {
                        this.copyToRealm(partner)
                    }

                    Resource.Success(result.isValid())
                }

            }catch (e: RealmException){
                Resource.Error(e.message ?: "Error creating Delivery Partner")
            }
        }else{
            return Resource.Error("User not authenticated", false)
        }
    }

    override suspend fun updatePartner(
        newPartner: DeliveryPartner,
        partnerId: String,
    ): Resource<Boolean> {
        return try {
            val findPartnerByEmail = realm.query<PartnerRealm>("partnerEmail == $0 AND _id != $1", newPartner.deliveryPartnerEmail, partnerId).first().find()
            val findPartnerByPhone = realm.query<PartnerRealm>("partnerPhone == $0 AND _id != $1", newPartner.deliveryPartnerPhone, partnerId).first().find()

            if(findPartnerByEmail != null){
                Resource.Error("Partner email already exists", false)
            }else if(findPartnerByPhone != null){
                Resource.Error("Partner phone already exists", false)
            }else{
                realm.write {
                    val partner = this.query<PartnerRealm>("_id == $0", partnerId).first().find()
                    partner?.partnerName = newPartner.deliveryPartnerName
                    partner?.partnerEmail = newPartner.deliveryPartnerEmail
                    partner?.partnerPhone = newPartner.deliveryPartnerPhone
                    partner?.partnerPassword = newPartner.deliveryPartnerPassword
                    partner?.partnerStatus = newPartner.deliveryPartnerStatus
                    partner?.partnerType = newPartner.deliveryPartnerType
                    partner?.updated_at = System.currentTimeMillis().toString()
                }

                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update delivery partner.")
        }
    }

    override suspend fun deletePartner(partnerId: String): Resource<Boolean> {
        return try {
            realm.write {
                val partner: PartnerRealm = this.query<PartnerRealm>("_id == $0", partnerId).find().first()

                delete(partner)
            }

            Resource.Success(true)

        } catch (e: Exception){
            Resource.Error(e.message ?: "Failed to delete delivery partner")
        }
    }
}