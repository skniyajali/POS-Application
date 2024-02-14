package com.niyaj.data.data.repository

import android.util.Patterns
import com.niyaj.common.tags.ProfileTestTags
import com.niyaj.common.tags.ProfileTestTags.ADDRESS_EMPTY_ERROR
import com.niyaj.common.tags.ProfileTestTags.DESC_EMPTY_ERROR
import com.niyaj.common.tags.ProfileTestTags.EMAIL_EMPTY_ERROR
import com.niyaj.common.tags.ProfileTestTags.EMAIL_NOT_VALID
import com.niyaj.common.tags.ProfileTestTags.NAME_EMPTY_ERROR
import com.niyaj.common.tags.ProfileTestTags.PASSWORD_EMPTY_ERROR
import com.niyaj.common.tags.ProfileTestTags.PASSWORD_INVALID_ERROR
import com.niyaj.common.tags.ProfileTestTags.P_PHONE_CHAR_ERROR
import com.niyaj.common.tags.ProfileTestTags.P_PHONE_EMPTY_ERROR
import com.niyaj.common.tags.ProfileTestTags.P_PHONE_LENGTH_ERROR
import com.niyaj.common.tags.ProfileTestTags.QR_CODE_ERROR
import com.niyaj.common.tags.ProfileTestTags.S_PHONE_CHAR_ERROR
import com.niyaj.common.tags.ProfileTestTags.S_PHONE_EMPTY_ERROR
import com.niyaj.common.tags.ProfileTestTags.S_PHONE_LENGTH_ERROR
import com.niyaj.common.tags.ProfileTestTags.TAG_EMPTY_ERROR
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.common.utils.isValidPassword
import com.niyaj.data.repository.RestaurantInfoRepository
import com.niyaj.data.repository.validation.RestaurantInfoValidationRepository
import com.niyaj.database.model.RestaurantInfoEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.RESTAURANT_ID
import com.niyaj.model.RestaurantInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import timber.log.Timber

class RestaurantInfoRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher
) : RestaurantInfoRepository, RestaurantInfoValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Profile Session")
    }

    override fun getRestaurantInfo(): Flow<RestaurantInfo> {
        return channelFlow {
            try {
                val info =
                    realm.query<RestaurantInfoEntity>("restaurantId == $0", RESTAURANT_ID).first()
                        .asFlow()

                info.collectLatest { result ->
                    when (result) {
                        is InitialObject -> {
                            send(result.obj.toExternalModel())
                        }

                        is UpdatedObject -> {
                            send(result.obj.toExternalModel())
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                send(RestaurantInfo())
            }
        }
    }

    override suspend fun updateRestaurantLogo(imageName: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val restaurant =
                        this.query<RestaurantInfoEntity>("restaurantId == $0", RESTAURANT_ID)
                            .first().find()

                    if (restaurant != null) {
                        restaurant.logo = imageName
                        restaurant.updatedAt = System.currentTimeMillis().toString()
                    } else {
                        val newRestaurant = RestaurantInfoEntity()
                        newRestaurant.restaurantId = RESTAURANT_ID
                        newRestaurant.logo = imageName
                        newRestaurant.createdAt = System.currentTimeMillis().toString()

                        this.copyToRealm(newRestaurant)
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update restaurant image")
        }
    }

    override suspend fun updatePrintLogo(imageName: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val restaurant =
                        this.query<RestaurantInfoEntity>("restaurantId == $0", RESTAURANT_ID)
                            .first().find()

                    if (restaurant != null) {
                        restaurant.printLogo = imageName
                        restaurant.updatedAt = System.currentTimeMillis().toString()
                    } else {
                        val newRestaurant = RestaurantInfoEntity()
                        newRestaurant.restaurantId = RESTAURANT_ID
                        newRestaurant.printLogo = imageName
                        newRestaurant.createdAt = System.currentTimeMillis().toString()

                        this.copyToRealm(newRestaurant)
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update restaurant print image")
        }
    }

    override suspend fun updateRestaurantInfo(restaurantInfo: RestaurantInfo): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validatedName = validateRestaurantName(restaurantInfo.name)
                val validatedTagLine = validateRestaurantTagline(restaurantInfo.tagline)
                val validatedEmail = validateRestaurantEmail(restaurantInfo.email)
                val validatedPrimaryPhone = validatePrimaryPhone(restaurantInfo.primaryPhone)
                val validatedSecondaryPhone = validateSecondaryPhone(restaurantInfo.secondaryPhone)
                val validatedAddress = validateRestaurantAddress(restaurantInfo.address)
                val validatedPaymentQrCode = validatePaymentQrCode(restaurantInfo.paymentQrCode)

                val hasError = listOf(
                    validatedName,
                    validatedTagLine,
                    validatedEmail,
                    validatedPrimaryPhone,
                    validatedSecondaryPhone,
                    validatedAddress,
                    validatedPaymentQrCode
                ).any { !it.successful }

                if (!hasError) {
                    realm.write {
                        val restaurant =
                            this.query<RestaurantInfoEntity>("restaurantId == $0", RESTAURANT_ID)
                                .first().find()

                        if (restaurant != null) {
                            restaurant.name = restaurantInfo.name
                            restaurant.tagline = restaurantInfo.tagline
                            restaurant.email = restaurantInfo.email
                            restaurant.primaryPhone = restaurantInfo.primaryPhone
                            restaurant.secondaryPhone = restaurantInfo.secondaryPhone
                            restaurant.description = restaurantInfo.description
                            restaurant.paymentQrCode = restaurantInfo.paymentQrCode
                            restaurant.address = restaurantInfo.address
                            restaurant.logo = restaurantInfo.logo
                            restaurant.printLogo = restaurantInfo.printLogo
                            restaurant.updatedAt = restaurantInfo.updatedAt
                        } else {
                            val newRestaurant = RestaurantInfoEntity()
                            newRestaurant.restaurantId = RESTAURANT_ID
                            newRestaurant.name = restaurantInfo.name
                            newRestaurant.tagline = restaurantInfo.tagline
                            newRestaurant.email = restaurantInfo.email
                            newRestaurant.primaryPhone = restaurantInfo.primaryPhone
                            newRestaurant.secondaryPhone = restaurantInfo.secondaryPhone
                            newRestaurant.description = restaurantInfo.description
                            newRestaurant.paymentQrCode = restaurantInfo.paymentQrCode
                            newRestaurant.address = restaurantInfo.address
                            newRestaurant.logo = restaurantInfo.logo
                            newRestaurant.printLogo = restaurantInfo.printLogo
                            newRestaurant.createdAt = System.currentTimeMillis().toString()

                            this.copyToRealm(newRestaurant)
                        }
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to validate restaurant info")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update restaurant info")
        }
    }

    override fun validatePaymentQrCode(paymentQrCode: String): ValidationResult {
        if (paymentQrCode.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = QR_CODE_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validatePrimaryPhone(primaryPhone: String): ValidationResult {
        if (primaryPhone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = P_PHONE_EMPTY_ERROR
            )
        }

        if (primaryPhone.any { it.isLetter() }) {
            return ValidationResult(
                successful = false,
                errorMessage = P_PHONE_CHAR_ERROR
            )
        }

        if (primaryPhone.length != 10) {
            return ValidationResult(
                successful = false,
                errorMessage = P_PHONE_LENGTH_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateRestaurantDesc(description: String): ValidationResult {
        if (description.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = DESC_EMPTY_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateRestaurantAddress(address: String): ValidationResult {
        if (address.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDRESS_EMPTY_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateRestaurantEmail(email: String): ValidationResult {
        if (email.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EMAIL_EMPTY_ERROR
            )
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = EMAIL_NOT_VALID
            )
        }

        return ValidationResult(true)
    }

    override fun validateRestaurantName(name: String): ValidationResult {
        if (name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = NAME_EMPTY_ERROR
            )
        }

        if (name.length < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.NAME_LENGTH_ERROR
            )
        }

        if (name.any { it.isDigit() }) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.NAME_DIGITS_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateRestaurantTagline(tagline: String): ValidationResult {
        if (tagline.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = TAG_EMPTY_ERROR
            )
        }

        if (tagline.length < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = ProfileTestTags.TAG_LENGTH_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateSecondaryPhone(secondaryPhone: String): ValidationResult {
        if (secondaryPhone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = S_PHONE_EMPTY_ERROR
            )
        }

        if (secondaryPhone.any { it.isLetter() }) {
            return ValidationResult(
                successful = false,
                errorMessage = S_PHONE_CHAR_ERROR
            )
        }

        if (secondaryPhone.length != 10) {
            return ValidationResult(
                successful = false,
                errorMessage = S_PHONE_LENGTH_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validatePassword(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PASSWORD_EMPTY_ERROR
            )
        }

        if (!isValidPassword(password)) {
            return ValidationResult(
                successful = false,
                errorMessage = PASSWORD_INVALID_ERROR
            )
        }


        return ValidationResult(true)
    }
}