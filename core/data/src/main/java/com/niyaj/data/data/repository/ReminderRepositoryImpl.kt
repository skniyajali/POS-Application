package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.utils.Constants.ABSENT_REMINDER_ID
import com.niyaj.common.utils.Constants.DAILY_SALARY_REMINDER_ID
import com.niyaj.common.utils.toDailySalaryAmount
import com.niyaj.common.utils.toRupee
import com.niyaj.data.repository.ReminderRepository
import com.niyaj.data.utils.collectAndSend
import com.niyaj.database.model.AttendanceEntity
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.PaymentEntity
import com.niyaj.database.model.ReminderEntity
import com.niyaj.database.model.toAbsentReminder
import com.niyaj.database.model.toDailySalaryReminder
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.AbsentReminder
import com.niyaj.model.DailySalaryReminder
import com.niyaj.model.EmployeeReminderWithStatus
import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.PaymentStatus
import com.niyaj.model.Reminder
import com.niyaj.model.ReminderType
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import timber.log.Timber

class ReminderRepositoryImpl(
    val config: RealmConfiguration,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : ReminderRepository {

    val realm = Realm.open(config)

    override fun getAllReminders(): Flow<List<Reminder>> {
        return channelFlow {
            try {
                withContext(ioDispatcher) {
                    val reminders = realm.query<ReminderEntity>().find().asFlow()

                    reminders.collectAndSend(
                        transform = { it.toExternalModel() },
                        send = { send(it) }
                    )
                }
            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    override suspend fun getAbsentReminder(): AbsentReminder? {
        val findReminder = withContext(ioDispatcher) {
            realm.query<ReminderEntity>("reminderId == $0", ABSENT_REMINDER_ID).first().find()
        }

        return findReminder?.toAbsentReminder()
    }

    override suspend fun getDailySalaryReminder(): DailySalaryReminder? {
        val reminder = withContext(ioDispatcher) {
            realm.query<ReminderEntity>("reminderId == $0", DAILY_SALARY_REMINDER_ID).first().find()
        }

        return reminder?.toDailySalaryReminder()
    }

    override suspend fun updateReminderAsNotCompleted(reminderId: String): Boolean {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val findReminder =
                        this.query<ReminderEntity>("reminderId == $0", reminderId).first().find()
                    findReminder?.isCompleted = false
                    findReminder?.updatedAt = System.currentTimeMillis().toString()
                }
            }

            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun deleteReminder(reminderId: String): Boolean {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val findReminder =
                        this.query<ReminderEntity>("reminderId == $0", reminderId).first().find()

                    findReminder?.let {
                        delete(it)
                    }
                }
            }

            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun createOrUpdateReminder(reminder: Reminder): Boolean {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val findReminder =
                        this.query<ReminderEntity>("reminderId == $0", reminder.reminderId).first()
                            .find()

                    if (findReminder != null) {
                        findReminder.reminderName = reminder.reminderName
                        findReminder.reminderStartTime = reminder.reminderStartTime
                        findReminder.reminderEndTime = reminder.reminderEndTime
                        findReminder.reminderInterval = reminder.reminderInterval
                        findReminder.reminderIntervalTimeUnit = reminder.reminderIntervalTimeUnit
                        findReminder.isRepeatable = reminder.isRepeatable
                        findReminder.isCompleted = reminder.isCompleted
                        findReminder.reminderType = reminder.reminderType
                        findReminder.updatedAt = System.currentTimeMillis().toString()
                    } else {
                        val newReminder = ReminderEntity()
                        newReminder.reminderId = reminder.reminderId
                        newReminder.reminderName = reminder.reminderName
                        newReminder.reminderStartTime = reminder.reminderStartTime
                        newReminder.reminderEndTime = reminder.reminderEndTime
                        newReminder.reminderInterval = reminder.reminderInterval
                        newReminder.reminderIntervalTimeUnit = reminder.reminderIntervalTimeUnit
                        newReminder.isRepeatable = reminder.isRepeatable
                        newReminder.isCompleted = reminder.isCompleted
                        newReminder.reminderType = reminder.reminderType
                        newReminder.notificationId = reminder.notificationId
                        newReminder.updatedAt = System.currentTimeMillis().toString()

                        this.copyToRealm(newReminder)
                    }
                }
            }
            true

        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun getReminderEmployee(
        salaryDate: String,
        reminderType: ReminderType
    ): Flow<List<EmployeeReminderWithStatus>> {
        return channelFlow {
            try {
                withContext(ioDispatcher) {
                    val employees = when (reminderType) {
                        ReminderType.Attendance -> {
                            realm.query<EmployeeEntity>().find().asFlow()
                        }

                        ReminderType.DailySalary -> {
                            realm.query<EmployeeEntity>(
                                "employeeSalaryType == $0",
                                EmployeeSalaryType.Daily.name
                            ).find().asFlow()
                        }

                        else -> {
                            realm.query<EmployeeEntity>(
                                "employeeSalaryType == $0",
                                EmployeeSalaryType.Monthly.name
                            ).find().asFlow()
                        }
                    }

                    employees.collectLatest { result ->
                        when (result) {
                            is InitialResults -> {
                                send(resultsChange(result.list, reminderType, salaryDate))
                            }

                            is UpdatedResults -> {
                                send(resultsChange(result.list, reminderType, salaryDate))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    private fun resultsChange(
        result: RealmResults<EmployeeEntity>,
        reminderType: ReminderType,
        salaryDate: String
    ): List<EmployeeReminderWithStatus> {
        val employeeSalaryWithStatusList = mutableListOf<EmployeeReminderWithStatus>()

        result.forEach { employee ->
            val isPaid = realm.query<PaymentEntity>(
                "employee.employeeId == $0 && salaryGivenDate == $1",
                employee.employeeId,
                salaryDate
            ).first().find()

            val isAbsent = realm.query<AttendanceEntity>(
                "employee.employeeId == $0 && absentDate == $1",
                employee.employeeId,
                salaryDate
            ).first().find()

            val employeeSalary = when (reminderType) {
                ReminderType.Attendance -> employee.employeePhone
                ReminderType.DailySalary -> employee.employeeSalary.toDailySalaryAmount()
                else -> employee.employeeSalary.toRupee
            }

            val paymentStatus =
                if (isPaid != null) PaymentStatus.Paid else if (isAbsent != null) PaymentStatus.Absent else PaymentStatus.NotPaid

            val employeeSalaryWithStatus = EmployeeReminderWithStatus(
                employee = employee.toExternalModel(),
                paymentStatus = paymentStatus,
                absentStatus = isAbsent != null,
                reminderType = reminderType,
                employeeSalary = employeeSalary,
            )

            employeeSalaryWithStatusList.add(employeeSalaryWithStatus)
        }

        return employeeSalaryWithStatusList
    }
}
