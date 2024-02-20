package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Employee
import com.niyaj.model.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {

    suspend fun getAllEmployee(): Flow<List<Employee>>

    suspend fun getEmployeeById(employeeId: String): Employee?

    suspend fun getAllPayments(searchText: String): Flow<List<Payment>>

    suspend fun getPaymentById(paymentId: String): Resource<Payment?>

    suspend fun addOrUpdatePayment(newPayment: Payment, paymentId: String): Resource<Boolean>

    suspend fun deletePayments(paymentIds: List<String>): Resource<Boolean>
}