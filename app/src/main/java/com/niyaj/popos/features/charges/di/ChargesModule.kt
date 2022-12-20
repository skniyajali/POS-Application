package com.niyaj.popos.features.charges.di

import com.niyaj.popos.features.charges.domain.repository.ChargesRepository
import com.niyaj.popos.features.charges.domain.use_cases.ChargesUseCases
import com.niyaj.popos.features.charges.domain.use_cases.CreateNewCharges
import com.niyaj.popos.features.charges.domain.use_cases.DeleteCharges
import com.niyaj.popos.features.charges.domain.use_cases.FindChargesByName
import com.niyaj.popos.features.charges.domain.use_cases.GetAllCharges
import com.niyaj.popos.features.charges.domain.use_cases.GetChargesById
import com.niyaj.popos.features.charges.domain.use_cases.UpdateCharges
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
    fun provideChargesUseCases(chargesRepository: ChargesRepository): ChargesUseCases {
        return ChargesUseCases(
            getAllCharges = GetAllCharges(chargesRepository),
            getChargesById = GetChargesById(chargesRepository),
            findChargesByName = FindChargesByName(chargesRepository),
            createNewCharges = CreateNewCharges(chargesRepository),
            updateCharges = UpdateCharges(chargesRepository),
            deleteCharges = DeleteCharges(chargesRepository),
        )
    }
}