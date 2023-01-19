package com.niyaj.popos.features.charges.di

import com.niyaj.popos.features.charges.domain.repository.ChargesRepository
import com.niyaj.popos.features.charges.domain.repository.ChargesValidationRepository
import com.niyaj.popos.features.charges.domain.use_cases.ChargesUseCases
import com.niyaj.popos.features.charges.domain.use_cases.CreateNewCharges
import com.niyaj.popos.features.charges.domain.use_cases.DeleteCharges
import com.niyaj.popos.features.charges.domain.use_cases.GetAllCharges
import com.niyaj.popos.features.charges.domain.use_cases.GetChargesById
import com.niyaj.popos.features.charges.domain.use_cases.UpdateCharges
import com.niyaj.popos.features.charges.domain.use_cases.validation.ValidateChargesName
import com.niyaj.popos.features.charges.domain.use_cases.validation.ValidateChargesPrice
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChargesModule {

    @Provides
    @Singleton
    fun provideChargesUseCases(chargesRepository: ChargesRepository, chargesValidationRepository: ChargesValidationRepository): ChargesUseCases {
        return ChargesUseCases(
            validateChargesName = ValidateChargesName(chargesValidationRepository),
            validateChargesPrice = ValidateChargesPrice(chargesValidationRepository),
            getAllCharges = GetAllCharges(chargesRepository),
            getChargesById = GetChargesById(chargesRepository),
            createNewCharges = CreateNewCharges(chargesRepository),
            updateCharges = UpdateCharges(chargesRepository),
            deleteCharges = DeleteCharges(chargesRepository),
        )
    }
}