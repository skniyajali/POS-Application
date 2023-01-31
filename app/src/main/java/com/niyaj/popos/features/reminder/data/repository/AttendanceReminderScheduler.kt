package com.niyaj.popos.features.reminder.data.repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.niyaj.popos.features.reminder.domain.model.AbsentReminder
import com.niyaj.popos.features.reminder.domain.repository.AttendanceReceiver
import com.niyaj.popos.features.reminder.domain.repository.ReminderScheduler
import com.niyaj.popos.features.reminder.domain.use_cases.ReminderUseCases
import com.niyaj.popos.util.getStartTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AttendanceReminderScheduler @Inject constructor(
    private val context: Context
): ReminderScheduler {

    @Inject lateinit var reminderUseCases : ReminderUseCases

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("MissingPermission")
    override suspend fun schedule(newReminder : AbsentReminder) {
        val reminder = reminderUseCases.getAbsentReminder()

        if (reminder.reminderStartTime <= getStartTime) {
            reminderUseCases.createOrUpdateAbsentReminder(newReminder)

            return
        } else {
            if (!reminder.isCompleted) {
                val intent = Intent(context, AttendanceReceiver::class.java).apply {
                    putExtra("EXTRA_MESSAGE", newReminder.reminderName)
                }

                val intervalMillis = TimeUnit.MILLISECONDS.convert(newReminder.reminderInterval.toLong(), TimeUnit.valueOf(newReminder.reminderIntervalTimeUnit))

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis().plus(300L),
                    intervalMillis,
                    PendingIntent.getBroadcast(
                        context,
                        newReminder.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
        }
    }

    override fun cancel(createdReminder : AbsentReminder) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                createdReminder.hashCode(),
                Intent(context, AttendanceReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

}