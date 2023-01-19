package com.niyaj.popos.features.address.di

import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.address.domain.repository.AddressValidationRepository
import com.niyaj.popos.features.address.domain.use_cases.AddressUseCases
import com.niyaj.popos.features.address.domain.use_cases.CreateNewAddress
import com.niyaj.popos.features.address.domain.use_cases.DeleteAddress
import com.niyaj.popos.features.address.domain.use_cases.GetAddressById
import com.niyaj.popos.features.address.domain.use_cases.GetAllAddress
import com.niyaj.popos.features.address.domain.use_cases.UpdateAddress
import com.niyaj.popos.features.address.domain.use_cases.validation.ValidateAddressName
import com.niyaj.popos.features.address.domain.use_cases.validation.ValidateAddressShortName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddressModule {

//    private val schema = setOf(Address::class)
//
//    private val config = RealmConfiguration
//        .Builder(schema)
//        .deleteRealmIfMigrationNeeded()
//        .name("popos.realm")
//        .log(LogLevel.ALL)
//        .build()
//
//
//    @Provides
//    fun provideAddressRealmDaoImpl(): AddressRepository {
//        return AddressRepositoryImpl(config)
//    }


    @Provides
    @Singleton
    fun provideAddressCases(addressRepository: AddressRepository, addressValidationRepository: AddressValidationRepository): AddressUseCases {
        return AddressUseCases(
            validateAddressName = ValidateAddressName(addressValidationRepository),
            validateAddressShortName = ValidateAddressShortName(addressValidationRepository),
            getAllAddress = GetAllAddress(addressRepository),
            getAddressById = GetAddressById(addressRepository),
            createNewAddress = CreateNewAddress(addressRepository),
            updateAddress = UpdateAddress(addressRepository),
            deleteAddress = DeleteAddress(addressRepository),
        )
    }
}