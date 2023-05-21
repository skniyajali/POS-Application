package com.niyaj.popos.features.charges.di

import com.niyaj.popos.features.charges.domain.repository.ChargesRepository
import com.niyaj.popos.features.charges.domain.use_cases.GetAllCharges
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
    fun provideGetAllChargesUseCases(chargesRepository: ChargesRepository): GetAllCharges {
        return GetAllCharges(chargesRepository)
    }
}