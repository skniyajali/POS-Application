package com.niyaj.popos.features.address.di

import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.address.domain.use_cases.GetAllAddress
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddressModule {

    @Provides
    @Singleton
    fun provideGetAllAddressUseCase(addressRepository: AddressRepository): GetAllAddress {
        return GetAllAddress(addressRepository)
    }
}