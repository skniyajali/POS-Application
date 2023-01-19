package com.niyaj.popos.features.delivery_partner.data.repository

import android.util.Patterns
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerRepository
import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerValidationRepository
import com.niyaj.popos.util.isValidPassword
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
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
) : PartnerRepository, PartnerValidationRepository {

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

    override fun getPartnerByEmail(partnerEmail: String, partnerId: String?): Boolean {
        val findPartnerByEmail: DeliveryPartner? = if (partnerId.isNullOrEmpty()) {
            realm.query<DeliveryPartner>("partnerEmail == $0", partnerEmail).first().find()
        } else {
            realm.query<DeliveryPartner>(
                "partnerEmail == $0 AND partnerId != $1",
                partnerEmail,
                partnerId
            ).first().find()
        }

        return findPartnerByEmail != null
    }

    override fun getPartnerByPhone(partnerPhone: String, partnerId: String?): Boolean {
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

        return findPartnerByPhone != null
    }

    override suspend fun createNewPartner(newPartner: DeliveryPartner): Resource<Boolean> {
        return try {
            val validatePartnerName = validatePartnerName(newPartner.partnerName)
            val validatePartnerPhone = validatePartnerPhone(newPartner.partnerPhone)
            val validatePartnerEmail = validatePartnerEmail(newPartner.partnerEmail)
            val validatePartnerPassword = validatePartnerPassword(newPartner.partnerPassword)

            val hasError = listOf(validatePartnerName, validatePartnerPhone, validatePartnerEmail, validatePartnerPassword).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher){
                    val partner = DeliveryPartner()
                    partner.partnerId = newPartner.partnerId.ifEmpty { BsonObjectId().toHexString() }
                    partner.partnerName = newPartner.partnerName
                    partner.partnerEmail = newPartner.partnerEmail
                    partner.partnerPhone = newPartner.partnerPhone
                    partner.partnerPassword = newPartner.partnerPassword
                    partner.partnerStatus = newPartner.partnerStatus
                    partner.partnerType = newPartner.partnerType
                    partner.createdAt = newPartner.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        this.copyToRealm(partner)
                    }
                }

                Resource.Success(true)
            }else {
                Resource.Error("Unable to validate partner", false)
            }
        } catch (e: RealmException) {
            Resource.Error(e.message ?: "Error creating Delivery Partner", true)
        }
    }

    override suspend fun updatePartner(newPartner: DeliveryPartner, partnerId: String): Resource<Boolean> {
        return try {
            val validatePartnerName = validatePartnerName(newPartner.partnerName)
            val validatePartnerPhone = validatePartnerPhone(newPartner.partnerPhone)
            val validatePartnerEmail = validatePartnerEmail(newPartner.partnerEmail)
            val validatePartnerPassword = validatePartnerPassword(newPartner.partnerPassword)

            val hasError = listOf(validatePartnerName, validatePartnerPhone, validatePartnerEmail, validatePartnerPassword).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher){
                    val partner = realm.query<DeliveryPartner>("partnerId == $0", partnerId).first().find()

                    if (partner != null) {
                        realm.write {
                            findLatest(partner)?.apply {
                                this.partnerName = newPartner.partnerName
                                this.partnerEmail = newPartner.partnerEmail
                                this.partnerPhone = newPartner.partnerPhone
                                this.partnerPassword = newPartner.partnerPassword
                                this.partnerStatus = newPartner.partnerStatus
                                this.partnerType = newPartner.partnerType
                                this.updatedAt = System.currentTimeMillis().toString()
                            }
                        }

                        Resource.Success(true)
                    }else {
                        Resource.Error("Unable to find partner", false)
                    }
                }
            }else {
                Resource.Error("Unable to validate partner", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update delivery partner.", false)
        }
    }

    override suspend fun deletePartner(partnerId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val partner = realm.query<DeliveryPartner>("partnerId == $0", partnerId).first().find()

                if (partner != null) {
                    realm.write {
                        findLatest(partner)?.let {
                            delete(it)
                        }
                    }

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to find partner", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete delivery partner", false)
        }
    }

    override fun validatePartnerName(partnerName: String): ValidationResult {
        if(partnerName.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Name is required"
            )
        }

        if(partnerName.length < 4){
            return ValidationResult(
                successful = false,
                errorMessage = "Name must be at least 4 characters long"
            )
        }

        if(partnerName.any { it.isDigit() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Name must not contain any digit"
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validatePartnerEmail(partnerEmail: String, partnerId: String?): ValidationResult {

        if(partnerEmail.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Email is required"
            )
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(partnerEmail).matches()){
            return ValidationResult(
                successful = false,
                errorMessage = "Email is invalid"
            )
        }

        val validationResult = getPartnerByEmail(partnerEmail, partnerId)

        if (validationResult) {
            return ValidationResult(
                successful = false,
                errorMessage = "Email already exists"
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validatePartnerPassword(partnerPassword: String): ValidationResult {
        if(partnerPassword.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Password is required"
            )
        }

        if(!isValidPassword(partnerPassword)){
            return ValidationResult(
                successful = false,
                errorMessage = "Password must be at least 8 characters long and it must contain a lowercase & uppercase letter and at least one special character and one digit."
            )
        }


        return ValidationResult(
            successful = true
        )
    }

    override fun validatePartnerPhone(partnerPhone: String, partnerId: String?): ValidationResult {

        if(partnerPhone.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Phone no is required"
            )
        }

        if(partnerPhone.length < 10 || partnerPhone.length > 10) {
            return ValidationResult(
                successful = false,
                errorMessage = "The phone no must be 10 digits",
            )
        }

        if(partnerPhone.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Phone no must not contain any letter"
            )
        }

        val validationResult = getPartnerByPhone(partnerPhone, partnerId)

        if(validationResult){
            return ValidationResult(
                false,
                errorMessage = "Phone no already exists",
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}