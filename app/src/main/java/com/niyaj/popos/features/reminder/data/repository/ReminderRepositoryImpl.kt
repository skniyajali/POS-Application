package com.niyaj.popos.features.reminder.data.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reminder.domain.model.ABSENT_REMINDER_ID
import com.niyaj.popos.features.reminder.domain.model.AbsentReminder
import com.niyaj.popos.features.reminder.domain.model.Reminder
import com.niyaj.popos.features.reminder.domain.model.toAbsentReminder
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import timber.log.Timber

class ReminderRepositoryImpl(
    val config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReminderRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Reminder Session")
    }

    override fun getAllReminders() : Flow<Resource<List<Reminder>>> {
        return channelFlow {
            try {
                withContext(ioDispatcher) {
                    send(Resource.Loading(true))
                    val reminders = realm.query<Reminder>().find().asFlow()

                    reminders.collectLatest {
                        when (it) {
                            is UpdatedResults -> {
                                send(Resource.Success(it.list))
                                send(Resource.Loading(false))

                            }
                            is InitialResults -> {
                                send(Resource.Success(it.list))
                                send(Resource.Loading(false))

                            }
                        }
                    }
                }
            }catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get all reminders", emptyList()))
            }
        }
    }

    override fun getAttendanceReminder(): AbsentReminder {
        val findReminder = realm.query<Reminder>("reminderId == $0", ABSENT_REMINDER_ID).first().find()

        return findReminder?.toAbsentReminder() ?: AbsentReminder()
    }

    override suspend fun createOrUpdateAttendanceReminder(absentReminder: AbsentReminder): Boolean {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val findReminder = this.query<Reminder>("reminderId == $0", ABSENT_REMINDER_ID).first().find()

                    if (findReminder != null) {
                        findReminder.reminderName = absentReminder.reminderName
                        findReminder.reminderStartTime = absentReminder.reminderStartTime
                        findReminder.reminderEndTime = absentReminder.reminderEndTime
                        findReminder.reminderInterval = absentReminder.reminderInterval
                        findReminder.reminderIntervalTimeUnit = absentReminder.reminderIntervalTimeUnit
                        findReminder.isRepeatable = absentReminder.isRepeatable
                        findReminder.isCompleted = absentReminder.isCompleted
                        findReminder.reminderType = absentReminder.reminderType
                        findReminder.updatedAt = System.currentTimeMillis().toString()
                    } else {
                        val reminder = Reminder()
                        reminder.reminderId = absentReminder.attendanceRemId
                        reminder.reminderName = absentReminder.reminderName
                        reminder.reminderStartTime = absentReminder.reminderStartTime
                        reminder.reminderEndTime = absentReminder.reminderEndTime
                        reminder.reminderInterval = absentReminder.reminderInterval
                        reminder.reminderIntervalTimeUnit = absentReminder.reminderIntervalTimeUnit
                        reminder.isRepeatable = absentReminder.isRepeatable
                        reminder.isCompleted = absentReminder.isCompleted
                        reminder.reminderType = absentReminder.reminderType
                        reminder.updatedAt = System.currentTimeMillis().toString()

                        this.copyToRealm(reminder)
                    }
                }
            }
            true

        }catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun createOrUpdateDailySalaryReminder(
        newReminder : Reminder,
        reminderId : String?
    ) : Boolean {
        TODO("Not yet implemented")
    }
}
