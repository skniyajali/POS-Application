package com.niyaj.popos.features.charges.data.repository

import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.domain.repository.ChargesRepository
import com.niyaj.popos.features.charges.domain.repository.ChargesValidationRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
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

class ChargesRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ChargesRepository, ChargesValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("ChargesDaoImpl Session")
    }

    override suspend fun getAllCharges(): Flow<Resource<List<Charges>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val charges = realm.query<Charges>().sort("chargesId", Sort.DESCENDING).find()

                    val items = charges.asFlow()

                    items.collect { changes: ResultsChange<Charges> ->
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
                    send(Resource.Error(e.message ?: "Unable to get charges items", emptyList()))
                }
            }
        }
    }

    override suspend fun getChargesById(chargesId: String): Resource<Charges?> {
        return try {
            val chargesItem = withContext(ioDispatcher) {
                realm.query<Charges>("chargesId == $0", chargesId).first().find()
            }

            Resource.Success(chargesItem)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Charges", null)
        }
    }

    override fun findChargesByName(chargesName: String, chargesId: String?): Boolean {
        val charges = if (chargesId.isNullOrEmpty()) {
            realm.query<Charges>("chargesName == $0", chargesName).first().find()
        } else {
            realm.query<Charges>("chargesId != $0 && chargesName == $1", chargesId, chargesName)
                .first().find()
        }

        return charges != null
    }

    override suspend fun createNewCharges(newCharges: Charges): Resource<Boolean> {
        return try {
            val validateChargesName = validateChargesName(newCharges.chargesName, newCharges.chargesId)
            val validateChargesPrice = validateChargesPrice(newCharges.isApplicable, newCharges.chargesPrice)

            val hasError = listOf(validateChargesName, validateChargesPrice).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher){
                    val chargesItem = Charges()
                    chargesItem.chargesId = newCharges.chargesId.ifEmpty { BsonObjectId().toHexString() }
                    chargesItem.chargesName = newCharges.chargesName
                    chargesItem.chargesPrice = newCharges.chargesPrice
                    chargesItem.isApplicable = newCharges.isApplicable
                    chargesItem.createdAt = newCharges.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        this.copyToRealm(chargesItem)
                    }
                }

                Resource.Success(true)
            }else{
                Resource.Error( "Unable to create Charges Item", false)
            }
        } catch (e: RealmException) {
            Resource.Error(e.message ?: "Error creating Charges Item", false)
        }
    }

    override suspend fun updateCharges(newCharges: Charges, chargesId: String): Resource<Boolean> {
        return try {
            val validateChargesName = validateChargesName(newCharges.chargesName, chargesId)
            val validateChargesPrice = validateChargesPrice(newCharges.isApplicable, newCharges.chargesPrice)

            val hasError = listOf(validateChargesName, validateChargesPrice).any { !it.successful }

            if (!hasError) {
                val chargesItem = realm.query<Charges>("chargesId == $0", chargesId).first().find()

                if (chargesItem != null) {
                    withContext(ioDispatcher){
                        realm.write {
                            findLatest(chargesItem)?.apply {
                                this.chargesName = newCharges.chargesName
                                this.chargesPrice = newCharges.chargesPrice
                                this.isApplicable = newCharges.isApplicable
                                this.updatedAt = System.currentTimeMillis().toString()
                            }
                        }
                    }

                    Resource.Success(true)
                }else{
                    Resource.Error("Unable to find charges item", false)
                }
            }else {
                Resource.Error("Unable to valid charges item", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update charges item", false)
        }
    }

    override suspend fun deleteCharges(chargesId: String): Resource<Boolean> {
        return try {
            val chargesItem = realm.query<Charges>("chargesId == $0", chargesId).first().find()

            if (chargesItem != null) {
                withContext(ioDispatcher){
                    realm.write {
                        findLatest(chargesItem)?.let {
                            delete(it)
                        }
                    }
                }

                Resource.Success(true)
            }else {
                Resource.Error("Unable to find charges item", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete charges item", false)
        }
    }

    override fun validateChargesName(chargesName: String, chargesId: String?): ValidationResult {
        if(chargesName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Charges Name must not be empty",
            )
        }

        if(chargesName.length < 5 ){
            return ValidationResult(
                successful = false,
                errorMessage = "Charges Name must be more than 5 characters long",
            )
        }

        val result = chargesName.any { it.isDigit() }

        if (result){
            return ValidationResult(
                successful = false,
                errorMessage = "Charges Name must not contain a digit",
            )
        }

        if (this.findChargesByName(chargesName, chargesId)){
            return ValidationResult(
                successful = false,
                errorMessage = "Charges Name already exists.",
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateChargesPrice(doesApplicable: Boolean, chargesPrice: Int): ValidationResult {
        if(doesApplicable) {
            if(chargesPrice == 0){
                return ValidationResult(
                    successful = false,
                    errorMessage = "Charges price required."
                )
            }

            if(chargesPrice < 10){
                return ValidationResult(
                    successful = false,
                    errorMessage = "Charges Price must be greater than 10 rupees."
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }
}