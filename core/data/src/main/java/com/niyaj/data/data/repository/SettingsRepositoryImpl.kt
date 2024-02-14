package com.niyaj.data.data.repository

import com.niyaj.common.utils.Constants.SETTINGS_ID
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.common.utils.isContainsArithmeticCharacter
import com.niyaj.data.repository.SettingsRepository
import com.niyaj.data.repository.validation.SettingsValidationRepository
import com.niyaj.database.model.SettingsEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Settings
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher
) : SettingsRepository, SettingsValidationRepository {

    val realm = Realm.open(config)

    override fun getSetting(): Resource<Settings> {
        return try {
            val settings = realm.query<SettingsEntity>().first().find()

            if (settings == null) {
                Resource.Success(Settings())
            }else {
                Resource.Success(settings.toExternalModel())
            }
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get settings")
        }
    }

    override suspend fun updateSetting(newSettings: Settings): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                realm.write {
                    val settings = this.query<SettingsEntity>("settingsId == $0", SETTINGS_ID).first().find()

                    if (settings != null) {
                        settings.expensesDataDeletionInterval = newSettings.expensesDataDeletionInterval
                        settings.reportDataDeletionInterval = newSettings.reportDataDeletionInterval
                        settings.cartDataDeletionInterval = newSettings.cartDataDeletionInterval
                        settings.cartOrderDataDeletionInterval = newSettings.cartOrderDataDeletionInterval
                        settings.updatedAt = System.currentTimeMillis().toString()
                    }else {
                        val createdSettings = SettingsEntity()
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
            Resource.Error(e.message ?: "Unable to create settings")
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