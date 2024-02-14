package com.niyaj.feature.employee_attendance.add_edit

import com.niyaj.model.Employee

sealed interface AddEditAbsentEvent {

    data class OnSelectEmployee(val employee: Employee) : AddEditAbsentEvent

    data class AbsentDateChanged(val absentDate: String) : AddEditAbsentEvent

    data class AbsentReasonChanged(val absentReason: String) : AddEditAbsentEvent

    data object CreateOrUpdateAbsent : AddEditAbsentEvent
}