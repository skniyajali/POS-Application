package com.niyaj.popos.features.reminder.di

import com.niyaj.popos.features.reminder.data.repository.ReminderRepositoryImpl
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration

@Module
@InstallIn(SingletonComponent::class)
object ReminderModule {

    @Provides
    fun provideReminderRepositoryImpl(config: RealmConfiguration): ReminderRepository {
        return ReminderRepositoryImpl(config)
    }
}