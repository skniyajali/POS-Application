package com.niyaj.popos.features.profile.data.repository

import android.util.Patterns
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoRepository
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoValidationRepository
import com.niyaj.popos.util.Constants.RESTAURANT_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class RestaurantInfoRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RestaurantInfoRepository, RestaurantInfoValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Product Session")
    }

    override fun getRestaurantInfo(): Resource<RestaurantInfo> {
        return try {
            val info = realm.query<RestaurantInfo>("restaurantId == $0", RESTAURANT_ID).first().find()

            if (info != null) {
                Resource.Success(info)
            }else {
                Resource.Success(RestaurantInfo())
            }

        }catch (e: Exception){
            Resource.Error(e.message ?: "Unable to get restaurant info", RestaurantInfo())
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
                        val restaurant = this.query<RestaurantInfo>("restaurantId == $0", RESTAURANT_ID).first().find()

                        if (restaurant != null) {
                            restaurant.name = restaurantInfo.name
                            restaurant.tagline = restaurantInfo.tagline
                            restaurant.email = restaurantInfo.email
                            restaurant.primaryPhone = restaurantInfo.primaryPhone
                            restaurant.secondaryPhone = restaurantInfo.secondaryPhone
                            restaurant.description = restaurantInfo.description
                            restaurant.paymentQrCode = restaurantInfo.paymentQrCode
                            restaurant.logo = restaurantInfo.logo
                            restaurant.updatedAt = restaurantInfo.updatedAt
                        }else {
                            val newRestaurant = RestaurantInfo()
                            newRestaurant.restaurantId = RESTAURANT_ID
                            newRestaurant.name = restaurantInfo.name
                            newRestaurant.tagline = restaurantInfo.tagline
                            newRestaurant.email = restaurantInfo.email
                            newRestaurant.primaryPhone = restaurantInfo.primaryPhone
                            newRestaurant.secondaryPhone = restaurantInfo.secondaryPhone
                            newRestaurant.description = restaurantInfo.description
                            newRestaurant.paymentQrCode = restaurantInfo.paymentQrCode
                            newRestaurant.logo = restaurantInfo.logo
                            newRestaurant.createdAt = System.currentTimeMillis().toString()

                            this.copyToRealm(newRestaurant)
                        }
                    }

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to validate restaurant info", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update restaurant info", false)
        }
    }

    override fun validatePaymentQrCode(paymentQrCode: String): ValidationResult {
        if (paymentQrCode.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant payment QR code must not be empty"
            )
        }

        return ValidationResult(true)
    }

    override fun validatePrimaryPhone(primaryPhone: String): ValidationResult {
        if (primaryPhone.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant primary phone must not be empty"
            )
        }

        if (primaryPhone.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant primary phone should not contain any characters"
            )
        }

        if (primaryPhone.length != 10){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant primary phone must be 10 digits"
            )
        }

        return ValidationResult(true)
    }

    override fun validateRestaurantAddress(address: String): ValidationResult {
        if (address.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant address must not be empty"
            )
        }

        return ValidationResult(true)
    }

    override fun validateRestaurantEmail(email: String): ValidationResult {
        if (email.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant email must not be empty"
            )
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant email is not a valid email address.",
            )
        }

        return ValidationResult(true)
    }

    override fun validateRestaurantName(name: String): ValidationResult {
        if (name.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant name must not be empty"
            )
        }

        return ValidationResult(true)
    }

    override fun validateRestaurantTagline(tagline: String): ValidationResult {
        if (tagline.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant tagline must not be empty"
            )
        }

        return ValidationResult(true)
    }

    override fun validateSecondaryPhone(secondaryPhone: String): ValidationResult {
        if (secondaryPhone.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant secondary phone must not be empty"
            )
        }

        if (secondaryPhone.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant secondary phone should not contain any characters"
            )
        }

        if (secondaryPhone.length != 10){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant secondary phone must be 10 digits"
            )
        }

        return ValidationResult(true)
    }
}