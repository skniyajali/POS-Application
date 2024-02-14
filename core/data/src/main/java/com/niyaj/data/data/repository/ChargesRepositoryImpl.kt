package com.niyaj.data.data.repository

import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_DIGIT_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_EMPTY_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_LENGTH_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_PRICE_LESS_THAN_TEN_ERROR
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.data.repository.ChargesRepository
import com.niyaj.data.repository.validation.ChargesValidationRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Charges
import com.niyaj.model.filterCharges
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class ChargesRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher
) : ChargesRepository, ChargesValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("ChargesDaoImpl Session")
    }

    override suspend fun getAllCharges(searchText: String): Flow<List<Charges>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val charges = realm.query<ChargesEntity>()
                        .sort("chargesId", Sort.DESCENDING)
                        .find()
                        .asFlow()

                    charges.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterCharges(searchText) },
                        send = { send(it) }
                    )
                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    override suspend fun getChargesById(chargesId: String): Resource<Charges?> {
        return try {
            val chargesItem = withContext(ioDispatcher) {
                realm.query<ChargesEntity>("chargesId == $0", chargesId).first()
                    .find()
            }

            Resource.Success(chargesItem?.toExternalModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Charges")
        }
    }

    override fun findChargesByName(chargesName: String, chargesId: String?): Boolean {
        val charges = if (chargesId.isNullOrEmpty()) {
            realm.query<ChargesEntity>("chargesName == $0", chargesName).first()
                .find()
        } else {
            realm.query<ChargesEntity>(
                "chargesId != $0 && chargesName == $1",
                chargesId,
                chargesName
            )
                .first().find()
        }

        return charges != null
    }

    override suspend fun createNewCharges(newCharges: Charges): Resource<Boolean> {
        return try {
            val validateChargesName =
                validateChargesName(newCharges.chargesName, newCharges.chargesId)
            val validateChargesPrice =
                validateChargesPrice(newCharges.chargesPrice)

            val hasError = listOf(validateChargesName, validateChargesPrice).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val chargesItem = ChargesEntity()
                    chargesItem.chargesId =
                        newCharges.chargesId.ifEmpty { BsonObjectId().toHexString() }
                    chargesItem.chargesName = newCharges.chargesName
                    chargesItem.chargesPrice = newCharges.chargesPrice
                    chargesItem.isApplicable = newCharges.isApplicable
                    chargesItem.createdAt =
                        newCharges.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        this.copyToRealm(chargesItem)
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Unable to create Charges Item")
            }
        } catch (e: RealmException) {
            Resource.Error(e.message ?: "Error creating Charges Item")
        }
    }

    override suspend fun updateCharges(
        newCharges: Charges,
        chargesId: String
    ): Resource<Boolean> {
        return try {
            val validateChargesName = validateChargesName(newCharges.chargesName, chargesId)
            val validateChargesPrice =
                validateChargesPrice(newCharges.chargesPrice)

            val hasError = listOf(validateChargesName, validateChargesPrice).any { !it.successful }

            if (!hasError) {
                val chargesItem =
                    realm.query<ChargesEntity>("chargesId == $0", chargesId)
                        .first().find()

                if (chargesItem != null) {
                    withContext(ioDispatcher) {
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
                } else {
                    Resource.Error("Unable to find charges item")
                }
            } else {
                Resource.Error("Unable to valid charges item")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update charges item")
        }
    }

    override suspend fun deleteCharges(chargesId: String): Resource<Boolean> {
        return try {
            val chargesItem =
                realm.query<ChargesEntity>("chargesId == $0", chargesId).first()
                    .find()

            if (chargesItem != null) {
                withContext(ioDispatcher) {
                    realm.write {
                        findLatest(chargesItem)?.let {
                            delete(it)
                        }
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Unable to find charges item")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete charges item")
        }
    }

    override suspend fun deleteAllCharges(chargesIds: List<String>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                chargesIds.forEach { chargesId ->
                    val chargesItem = realm
                        .query<ChargesEntity>("chargesId == $0", chargesId)
                        .first()
                        .find()

                    if (chargesItem != null) {
                        withContext(ioDispatcher) {
                            realm.write {
                                findLatest(chargesItem)?.let {
                                    delete(it)
                                }
                            }
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete charges item")
        }
    }

    override fun validateChargesName(chargesName: String, chargesId: String?): ValidationResult {
        if (chargesName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = CHARGES_NAME_EMPTY_ERROR
            )
        }

        if (chargesName.length < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = CHARGES_NAME_LENGTH_ERROR
            )
        }

        val result = chargesName.any { it.isDigit() }

        if (result) {
            return ValidationResult(
                successful = false,
                errorMessage = CHARGES_NAME_DIGIT_ERROR
            )
        }

        if (this.findChargesByName(chargesName, chargesId)) {
            return ValidationResult(
                successful = false,
                errorMessage = CHARGES_NAME_ALREADY_EXIST_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateChargesPrice(chargesPrice: Int): ValidationResult {
        if (chargesPrice == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = CHARGES_PRICE_EMPTY_ERROR
            )
        }

        if (chargesPrice < 10) {
            return ValidationResult(
                successful = false,
                errorMessage = CHARGES_PRICE_LESS_THAN_TEN_ERROR
            )
        }


        return ValidationResult(
            successful = true
        )
    }
}