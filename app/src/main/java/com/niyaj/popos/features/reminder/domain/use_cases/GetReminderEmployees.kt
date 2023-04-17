package com.niyaj.popos.features.reminder.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reminder.domain.model.EmployeeReminderWithStatus
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import com.niyaj.popos.features.reminder.domain.util.ReminderType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReminderEmployees @Inject constructor(
    private val reminderRepository : ReminderRepository
) {
    suspend operator fun invoke(salaryDate: String, reminderType : ReminderType): Flow<Resource<List<EmployeeReminderWithStatus>>> {
        return reminderRepository.getDailySalaryEmployee(salaryDate, reminderType)
    }
}