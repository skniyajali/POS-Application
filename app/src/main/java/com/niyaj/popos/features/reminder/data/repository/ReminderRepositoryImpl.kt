package com.niyaj.popos.features.reminder.data.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.util.EmployeeSalaryType
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.reminder.domain.model.*
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import com.niyaj.popos.features.reminder.domain.util.PaymentStatus
import com.niyaj.popos.features.reminder.domain.util.ReminderType
import com.niyaj.popos.util.Constants.ABSENT_REMINDER_ID
import com.niyaj.popos.util.Constants.DAILY_SALARY_REMINDER_ID
import com.niyaj.popos.util.toDailySalaryAmount
import com.niyaj.popos.util.toRupee
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
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

    override fun getAbsentReminder(): AbsentReminder? {
        val findReminder = realm.query<Reminder>("reminderId == $0", ABSENT_REMINDER_ID).first().find()

        return findReminder?.toAbsentReminder()
    }

    override suspend fun getDailySalaryReminder() : DailySalaryReminder? {
        val reminder = withContext(ioDispatcher) {
            realm.query<Reminder>("reminderId == $0", DAILY_SALARY_REMINDER_ID).first().find()
        }

        return reminder?.toDailySalaryReminder()
    }

    override suspend fun updateReminderAsNotCompleted(reminderId : String) : Boolean {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val findReminder = this.query<Reminder>("reminderId == $0", reminderId).first().find()
                    findReminder?.isCompleted = false
                    findReminder?.updatedAt = System.currentTimeMillis().toString()
                }
            }

            true
        }catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun deleteReminder(reminderId : String) : Boolean {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val findReminder = this.query<Reminder>("reminderId == $0", reminderId).first().find()

                    findReminder?.let {
                        delete(it)
                    }
                }
            }

            true
        }catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun createOrUpdateReminder(reminder : Reminder) : Boolean {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val findReminder = this.query<Reminder>("reminderId == $0", reminder.reminderId).first().find()

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
                        val newReminder = Reminder()
                        newReminder.reminderId = reminder.reminderId
                        newReminder.reminderName = reminder.reminderName
                        newReminder.reminderStartTime = reminder.reminderStartTime
                        newReminder.reminderEndTime = reminder.reminderEndTime
                        newReminder.reminderInterval = reminder.reminderInterval
                        newReminder.reminderIntervalTimeUnit = reminder.reminderIntervalTimeUnit
                        newReminder.isRepeatable = reminder.isRepeatable
                        newReminder.isCompleted = reminder.isCompleted
                        newReminder.reminderType = reminder.reminderType
                        newReminder.updatedAt = System.currentTimeMillis().toString()

                        this.copyToRealm(newReminder)
                    }
                }
            }
            true

        }catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun getDailySalaryEmployee(
        salaryDate : String,
        reminderType : ReminderType
    ) : Flow<Resource<List<EmployeeReminderWithStatus>>> {
        return channelFlow {
            try {
                withContext(ioDispatcher) {
                    send(Resource.Loading(true))
                    val employees = when(reminderType) {
                        ReminderType.Attendance -> {
                            realm.query<Employee>().find().asFlow()
                        }
                        ReminderType.DailySalary -> {
                            realm.query<Employee>("employeeSalaryType == $0", EmployeeSalaryType.Daily.salaryType).find().asFlow()
                        }
                        ReminderType.MonthlySalary -> {
                            realm.query<Employee>("employeeSalaryType == $0", EmployeeSalaryType.Monthly.salaryType).find().asFlow()
                        }
                    }

                    employees.collectLatest { result ->
                        when(result) {
                            is InitialResults -> {
                                val employeeSalaryWithStatusList = resultsChange(result.list, reminderType, salaryDate)

                                send(Resource.Success(employeeSalaryWithStatusList))
                                send(Resource.Loading(false))
                            }
                            is UpdatedResults -> {
                                val employeeSalaryWithStatusList = resultsChange(result.list, reminderType, salaryDate)

                                send(Resource.Success(employeeSalaryWithStatusList))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                }
            }catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get all employee", emptyList()))
            }
        }
    }

    private fun resultsChange(
        result : RealmResults<Employee>,
        reminderType : ReminderType,
        salaryDate : String
    ): List<EmployeeReminderWithStatus> {
        val employeeSalaryWithStatusList = mutableListOf<EmployeeReminderWithStatus>()

        result.forEach { employee ->
            val isPaid = realm.query<EmployeeSalary>("employee.employeeId == $0 && salaryGivenDate == $1",employee.employeeId, salaryDate).first().find()

            val isAbsent = realm.query<EmployeeAttendance>("employee.employeeId == $0 && absentDate == $1",employee.employeeId, salaryDate).first().find()

            val employeeSalary = when(reminderType) {
                ReminderType.Attendance -> employee.employeePhone
                ReminderType.DailySalary -> employee.employeeSalary.toDailySalaryAmount()
                ReminderType.MonthlySalary -> employee.employeeSalary.toRupee
            }

            val paymentStatus = if (isPaid != null) PaymentStatus.Paid else if (isAbsent != null) PaymentStatus.Absent else PaymentStatus.NotPaid

            val employeeSalaryWithStatus = EmployeeReminderWithStatus(
                employee = employee,
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
