package com.niyaj.data.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.data.repository.validation.PaymentValidationRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.PaymentEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Employee
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import com.niyaj.model.filterEmployeeSalary
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class PaymentRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher
) : PaymentRepository, PaymentValidationRepository {

    val realm = Realm.open(config)

    override suspend fun getAllEmployee(): Flow<List<Employee>> {
        return withContext(ioDispatcher) {
            realm.query<EmployeeEntity>().find().asFlow().mapLatest { employees ->
                employees.collectWithSearch(
                    transform = { it.toExternalModel() },
                    searchFilter = { it }
                )
            }
        }
    }

    override suspend fun getEmployeeById(employeeId: String): Employee? {
        return try {
            withContext(ioDispatcher) {
                realm
                    .query<EmployeeEntity>("employeeId == $0", employeeId)
                    .first()
                    .find()
                    ?.toExternalModel()
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAllPayments(searchText: String): Flow<List<Payment>> {
        return withContext(ioDispatcher) {
            realm
                .query<PaymentEntity>()
                .sort("paymentDate", Sort.DESCENDING)
                .find()
                .asFlow()
                .mapLatest { salaries ->
                    salaries.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterEmployeeSalary(searchText) },
                    )
                }
        }
    }

    override suspend fun getPaymentById(paymentId: String): Resource<Payment?> {
        return try {
            val salary = withContext(ioDispatcher) {
                realm.query<PaymentEntity>("paymentId == $0", paymentId).first().find()
            }

            Resource.Success(salary?.toExternalModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Salary")
        }
    }

    override suspend fun addOrUpdatePayment(
        newPayment: Payment,
        paymentId: String
    ): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val validateEmployee = validateEmployee(newPayment.employee?.employeeId ?: "")
                val validateGivenDate = validateGivenDate(newPayment.paymentDate)
                val validatePaymentType = validatePaymentType(newPayment.paymentType)
                val validateSalary = validatePaymentAmount(newPayment.paymentAmount)
                val validateSalaryNote = validatePaymentNote(newPayment.paymentNote)
                val validateSalaryType = validatePaymentMode(newPayment.paymentMode)

                val hasError = listOf(
                    validateEmployee,
                    validateSalary,
                    validateSalaryNote,
                    validateSalaryType,
                    validatePaymentType,
                    validateGivenDate
                ).any { !it.successful }

                if (!hasError) {
                    val employee = realm.query<EmployeeEntity>(
                        "employeeId == $0",
                        newPayment.employee?.employeeId
                    ).first().find()

                    if (employee != null) {
                        val salary =
                            realm.query<PaymentEntity>("paymentId == $0", paymentId).first()
                                .find()

                        if (salary != null) {
                            realm.write {
                                findLatest(salary)?.apply {
                                    this.paymentAmount = newPayment.paymentAmount
                                    this.paymentMode = newPayment.paymentMode.name
                                    this.paymentDate = newPayment.paymentDate
                                    this.paymentType = newPayment.paymentType.name
                                    this.paymentNote = newPayment.paymentNote
                                    this.updatedAt = System.currentTimeMillis().toString()

                                    findLatest(employee)?.also {
                                        this.employee = it
                                    }
                                }
                            }
                            Resource.Success(true)
                        } else {
                            realm.write {
                                this.copyToRealm(newPayment.toEntity(findLatest(employee)))
                            }

                            Resource.Success(true)
                        }
                    } else {
                        Resource.Error("Unable to find employee")
                    }
                } else {
                    Resource.Error("Unable to validate employee salary")
                }

            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to update employee.")
            }
        }
    }

    override suspend fun deletePayments(paymentIds: List<String>): Resource<Boolean> {
        return try {
            paymentIds.forEach { paymentId ->
                withContext(ioDispatcher) {
                    val salary =
                        realm.query<PaymentEntity>("paymentId == $0", paymentId).first().find()

                    if (salary != null) {
                        realm.write {
                            findLatest(salary)?.let {
                                delete(it)
                            }
                        }
                    }
                }
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete salary.")
        }
    }

    override fun validateEmployee(employeeId: String): ValidationResult {
        if (employeeId.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must not be empty",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validateGivenDate(givenDate: String): ValidationResult {
        if (givenDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Given date must not be empty",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validatePaymentType(paymentType: PaymentType): ValidationResult {
        if (paymentType.name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Payment type must not be empty."
            )
        }

        return ValidationResult(true)
    }

    override fun validatePaymentAmount(paymentAmount: String): ValidationResult {
        if (paymentAmount.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Salary must not be empty",
            )
        }

        if (paymentAmount.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "Salary must greater than two digits",
            )
        }

        if (paymentAmount.any { it.isLetter() }) {
            return ValidationResult(
                successful = false,
                errorMessage = "Salary must not contain any characters",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validatePaymentNote(paymentNote: String, isRequired: Boolean): ValidationResult {
        if (isRequired) {
            if (paymentNote.isEmpty()) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Salary note required because you paid using Cash and Online."
                )
            }
        }

        return ValidationResult(true)
    }

    override fun validatePaymentMode(paymentMode: PaymentMode): ValidationResult {
        if (paymentMode.name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Salary type must not be empty",
            )
        }

        return ValidationResult(true)
    }
}