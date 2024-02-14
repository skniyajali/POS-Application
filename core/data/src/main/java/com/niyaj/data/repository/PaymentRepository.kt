package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.model.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {

    suspend fun getAllEmployee(): Flow<List<Employee>>

    suspend fun getEmployeeById(employeeId: String): Employee?

    fun getAllPayments(searchText: String): Flow<List<Payment>>

    suspend fun getPaymentById(paymentId: String): Resource<Payment?>

    suspend fun getPaymentByEmployeeId(employeeId: String, selectedDate: Pair<String, String>): Resource<EmployeeSalaryEstimation?>

    suspend fun addNewPayment(newPayment: Payment): Resource<Boolean>

    suspend fun updatePaymentById(newSalary: Payment, paymentId: String): Resource<Boolean>

    suspend fun deletePaymentById(paymentId: String): Resource<Boolean>

    suspend fun deletePayments(paymentIds: List<String>): Resource<Boolean>

    suspend fun getEmployeePayments(employeeId: String): Flow<Resource<List<EmployeePayments>>>

    suspend fun getPaymentCalculableDate(employeeId: String): Resource<List<EmployeeMonthlyDate>>
}