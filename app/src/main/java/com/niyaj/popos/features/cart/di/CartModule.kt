package com.niyaj.popos.features.cart.di

import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.cart.data.repository.CartRepositoryImpl
import com.niyaj.popos.features.cart.domain.repository.CartRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration

@Module
@InstallIn(SingletonComponent::class)
object CartModule {

    @Provides
    fun provideCartRepositoryImpl(
        config : RealmConfiguration,
        settingsRepository : SettingsRepository
    ): CartRepository {
        return CartRepositoryImpl(config, settingsRepository)
    }
}