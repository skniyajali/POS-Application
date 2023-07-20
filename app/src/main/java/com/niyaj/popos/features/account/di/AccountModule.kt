package com.niyaj.popos.features.account.di

import com.niyaj.popos.features.account.data.AccountRepositoryImpl
import com.niyaj.popos.features.account.domain.repository.AccountRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration

@Module
@InstallIn(SingletonComponent::class)
object AccountModule {

    @Provides
    fun provideAccountRepository(config: RealmConfiguration): AccountRepository {
        return AccountRepositoryImpl(config)
    }
}