package com.niyaj.popos.notifications.di

import com.niyaj.popos.notifications.Notifier
import com.niyaj.popos.notifications.SystemTrayNotifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsModule {
    @Binds
    abstract fun bindNotifier(
        notifier: SystemTrayNotifier,
    ): Notifier
}
