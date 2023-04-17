package com.niyaj.popos.features.reminder.di

import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import com.niyaj.popos.features.reminder.domain.use_cases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ReminderModule {

    @Provides
    fun provideReminderUseCases(reminderRepository : ReminderRepository): ReminderUseCases {
        return ReminderUseCases(
            getAllReminders = GetAllReminders(reminderRepository),
            getAbsentReminder = GetAbsentReminder(reminderRepository),
            getDailySalaryReminder = GetDailySalaryReminder(reminderRepository),
            createOrUpdateAbsentReminder = CreateOrUpdateAbsentReminder(reminderRepository),
            updateReminderAsNotCompleted = UpdateReminderAsNotCompleted(reminderRepository),
            createOrUpdateDailySalaryReminder = CreateOrUpdateDailySalaryReminder(reminderRepository),
            getReminderEmployees = GetReminderEmployees(reminderRepository),
            deleteReminder = DeleteReminder(reminderRepository)
        )
    }
}