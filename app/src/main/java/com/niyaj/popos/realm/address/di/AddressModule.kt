package com.niyaj.popos.realm.address.di

import com.niyaj.popos.realm.address.data.repository.AddressRepositoryImpl
import com.niyaj.popos.realm.address.domain.model.Address
import com.niyaj.popos.realm.address.domain.repository.AddressRepository
import com.niyaj.popos.realm.address.domain.use_cases.AddressUseCases
import com.niyaj.popos.realm.address.domain.use_cases.CreateNewAddress
import com.niyaj.popos.realm.address.domain.use_cases.DeleteAddress
import com.niyaj.popos.realm.address.domain.use_cases.GetAddressById
import com.niyaj.popos.realm.address.domain.use_cases.GetAllAddress
import com.niyaj.popos.realm.address.domain.use_cases.UpdateAddress
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.log.LogLevel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddressModule {

    private val schema = setOf(Address::class)

    private val config = RealmConfiguration
        .Builder(schema)
        .deleteRealmIfMigrationNeeded()
        .name("address.realm")
        .log(LogLevel.ALL)
        .build()


    @Provides
    fun provideAddressRealmDaoImpl(): AddressRepository {
        return AddressRepositoryImpl(config)
    }


    @Provides
    @Singleton
    fun provideAddressCases(addressRepository: AddressRepository): AddressUseCases {
        return AddressUseCases(
            getAllAddress = GetAllAddress(addressRepository),
            getAddressById = GetAddressById(addressRepository),
            createNewAddress = CreateNewAddress(addressRepository),
            updateAddress = UpdateAddress(addressRepository),
            deleteAddress = DeleteAddress(addressRepository),
        )
    }
}