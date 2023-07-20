package com.niyaj.popos.features.address.di

import com.niyaj.popos.features.address.data.repository.AddressRepositoryImpl
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.address.domain.repository.AddressValidationRepository
import com.niyaj.popos.features.address.domain.use_cases.GetAllAddress
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddressModule {

    @Provides
    fun provideAddressRepositoryImpl(config: RealmConfiguration): AddressRepository {
        return AddressRepositoryImpl(config)
    }

    @Provides
    fun provideAddressValidationRepositoryImpl(config: RealmConfiguration): AddressValidationRepository {
        return AddressRepositoryImpl(config)
    }

    @Provides
    @Singleton
    fun provideGetAllAddressUseCase(addressRepository: AddressRepository): GetAllAddress {
        return GetAllAddress(addressRepository)
    }
}