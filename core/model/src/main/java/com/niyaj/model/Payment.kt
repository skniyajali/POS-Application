package com.niyaj.model

import com.niyaj.common.utils.toSalaryDate

data class Payment(
    val paymentId: String = "",

    val employee: Employee? = null,

    val paymentAmount: String = "",

    val paymentDate: String = "",

    val paymentMode: PaymentMode = PaymentMode.Cash,

    val paymentType: PaymentType = PaymentType.Salary,

    val paymentNote: String = "",

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)


fun List<Payment>.filterEmployeeSalary(searchText: String): List<Payment> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.paymentAmount.contains(searchText, true) ||
                    it.paymentMode.name.contains(searchText, true) ||
                    it.paymentDate.toSalaryDate.contains(searchText, true) ||
                    it.paymentType.name.contains(searchText, true) ||
                    it.paymentNote.contains(searchText, true) ||
                    it.createdAt.toSalaryDate.contains(searchText, true) ||
                    it.updatedAt?.toSalaryDate?.contains(searchText, true) == true ||
                    it.employee?.employeeName?.contains(searchText, true) == true ||
                    it.employee?.employeePhone?.contains(searchText, true) == true ||
                    it.employee?.employeeType?.name?.contains(searchText, true) == true ||
                    it.employee?.employeePosition?.contains(searchText, true) == true
        }
    }else this
}