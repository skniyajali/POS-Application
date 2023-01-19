package com.niyaj.popos.features.app_settings.di

import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.app_settings.domain.repository.SettingsValidationRepository
import com.niyaj.popos.features.app_settings.domain.use_cases.GetSetting
import com.niyaj.popos.features.app_settings.domain.use_cases.SettingsUseCases
import com.niyaj.popos.features.app_settings.domain.use_cases.UpdateSetting
import com.niyaj.popos.features.app_settings.domain.use_cases.validation.ValidateCartInterval
import com.niyaj.popos.features.app_settings.domain.use_cases.validation.ValidateCartOrderInterval
import com.niyaj.popos.features.app_settings.domain.use_cases.validation.ValidateExpensesInterval
import com.niyaj.popos.features.app_settings.domain.use_cases.validation.ValidateReportsInterval
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    @Provides
    @Singleton
    fun provideSettingsUseCases(settingsRepository: SettingsRepository, settingsValidationRepository: SettingsValidationRepository): SettingsUseCases {
        return SettingsUseCases(
            getSetting = GetSetting(settingsRepository),
            updateSetting = UpdateSetting(settingsRepository),
            validateCartInterval = ValidateCartInterval(settingsValidationRepository),
            validateCartOrderInterval = ValidateCartOrderInterval(settingsValidationRepository),
            validateExpensesInterval = ValidateExpensesInterval(settingsValidationRepository),
            validateReportsInterval = ValidateReportsInterval(settingsValidationRepository),
        )
    }
}