package com.niyaj.popos.realm.employee_salary.presentation.add_edit

sealed class AddEditSalaryEvent {

    data class EmployeeChanged(val employeeId: String) : AddEditSalaryEvent()

    data class SalaryChanged(val salary: String) : AddEditSalaryEvent()

    data class SalaryTypeChanged(val salaryType: String) : AddEditSalaryEvent()

    data class SalaryDateChanged(val salaryDate: String) : AddEditSalaryEvent()

    data class PaymentTypeChanged(val paymentType: String) : AddEditSalaryEvent()

    data class SalaryNoteChanged(val salaryNote: String) : AddEditSalaryEvent()

    object AddSalaryEntry : AddEditSalaryEvent()

    data class UpdateSalaryEntry(val salaryId: String): AddEditSalaryEvent()
}
