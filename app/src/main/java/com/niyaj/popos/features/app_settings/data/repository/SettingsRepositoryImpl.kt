package com.niyaj.popos.features.app_settings.data.repository

import com.niyaj.popos.common.utils.Constants.SETTINGS_ID
import com.niyaj.popos.common.utils.isContainsArithmeticCharacter
import com.niyaj.popos.features.app_settings.domain.model.Settings
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.app_settings.domain.repository.SettingsValidationRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SettingsRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SettingsRepository, SettingsValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Settings Session")
    }


    override fun getSetting(): Resource<Settings> {
        return try {
            val settings = realm.query<Settings>().first().find()

            if (settings == null) {
                Resource.Success(Settings())
            }else {
                Resource.Success(settings)
            }
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get settings", Settings())
        }
    }

    override suspend fun updateSetting(newSettings: Settings): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                realm.write {
                    val settings = this.query<Settings>("settingsId == $0", SETTINGS_ID).first().find()

                    if (settings != null) {
                        settings.expensesDataDeletionInterval = newSettings.expensesDataDeletionInterval
                        settings.reportDataDeletionInterval = newSettings.reportDataDeletionInterval
                        settings.cartDataDeletionInterval = newSettings.cartDataDeletionInterval
                        settings.cartOrderDataDeletionInterval = newSettings.cartOrderDataDeletionInterval
                        settings.updatedAt = System.currentTimeMillis().toString()
                    }else {
                        val createdSettings = Settings()
                        createdSettings.expensesDataDeletionInterval = newSettings.expensesDataDeletionInterval
                        createdSettings.reportDataDeletionInterval = newSettings.reportDataDeletionInterval
                        createdSettings.cartDataDeletionInterval = newSettings.cartDataDeletionInterval
                        createdSettings.cartOrderDataDeletionInterval = newSettings.cartOrderDataDeletionInterval

                        this.copyToRealm(createdSettings)
                    }
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create settings", false)
        }
    }

    override fun validateCartInterval(cartsInterval: String): ValidationResult {
        if (cartsInterval.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Cart interval must not be empty"
            )
        }

        if (cartsInterval.isContainsArithmeticCharacter) {
            return ValidationResult(
                successful = false,
                errorMessage = "Cart interval is invalid"
            )
        }

        if (cartsInterval.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Cart interval must not contain a letter"
            )
        }

        try {
            if (cartsInterval.toInt() >= 15) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Cart interval must be between 15 days."
                )
            }
        }catch (e: Exception) {
            return ValidationResult(
                successful = false,
                errorMessage = "Cart interval is invalid"
            )
        }

        return ValidationResult(true)
    }

    override fun validateCartOrderInterval(cartOrderInterval: String): ValidationResult {

        if (cartOrderInterval.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "CartOrder interval must not be empty"
            )
        }

        if (cartOrderInterval.isContainsArithmeticCharacter) {
            return ValidationResult(
                successful = false,
                errorMessage = "CartOrder interval is invalid"
            )
        }

        if (cartOrderInterval.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "CartOrder interval must not contain a letter"
            )
        }

        try {
            if (cartOrderInterval.toInt() >= 15) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "CartOrder interval must be between 15 days."
                )
            }
        }catch (e: Exception) {
            return ValidationResult(
                successful = false,
                errorMessage = "CartOrder interval is invalid"
            )
        }

        return ValidationResult(true)
    }

    override fun validateExpensesInterval(expensesInterval: String): ValidationResult {
        if (expensesInterval.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Expenses interval must not be empty"
            )
        }

        if (expensesInterval.isContainsArithmeticCharacter) {
            return ValidationResult(
                successful = false,
                errorMessage = "Expenses interval is invalid"
            )
        }

        if (expensesInterval.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Expenses interval must not contain a letter"
            )
        }

        try {
            if (expensesInterval.toInt() >= 15) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Expenses interval must be between 15 days."
                )
            }
        }catch (e: Exception) {
            return ValidationResult(
                successful = false,
                errorMessage = "Expenses interval is invalid"
            )
        }


        return ValidationResult(true)
    }

    override fun validateReportsInterval(reportsInterval: String): ValidationResult {

        if (reportsInterval.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Reports interval must not be empty"
            )
        }

        if (reportsInterval.isContainsArithmeticCharacter) {
            return ValidationResult(
                successful = false,
                errorMessage = "Reports interval is invalid"
            )
        }

        if (reportsInterval.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Reports interval must not contain a letter"
            )
        }

        try {
            if (reportsInterval.toInt() >= 15) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Reports interval must be between 7-15 days."
                )
            }

            if (reportsInterval.toInt() < 7) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Reports interval must be between 7-15 days."
                )
            }
        }catch (e: Exception) {
            return ValidationResult(
                successful = false,
                errorMessage = "Reports interval is invalid"
            )
        }

        return ValidationResult(true)
    }
}