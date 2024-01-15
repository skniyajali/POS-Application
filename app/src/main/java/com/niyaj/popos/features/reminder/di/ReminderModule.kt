package com.niyaj.popos.features.reminder.di

import com.niyaj.popos.common.network.Dispatcher
import com.niyaj.popos.common.network.PoposDispatchers
import com.niyaj.popos.features.reminder.data.repository.ReminderRepositoryImpl
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ReminderModule {

    @Provides
    fun provideReminderRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher,
    ): ReminderRepository {
        return ReminderRepositoryImpl(config, ioDispatcher)
    }
}