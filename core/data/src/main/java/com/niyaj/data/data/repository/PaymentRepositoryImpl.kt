package com.niyaj.data.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.common.utils.compareSalaryDates
import com.niyaj.common.utils.getSalaryDates
import com.niyaj.common.utils.toRupee
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.data.repository.validation.PaymentValidationRepository
import com.niyaj.data.utils.collectAndSend
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.AttendanceEntity
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.PaymentEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentStatus
import com.niyaj.model.PaymentType
import com.niyaj.model.filterEmployeeSalary
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class PaymentRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher
) : PaymentRepository, PaymentValidationRepository {

    val realm = Realm.open(config)

    override suspend fun getAllEmployee(): Flow<List<Employee>> {
        return channelFlow {
            try {
                withContext(ioDispatcher) {
                    val employees = realm.query<EmployeeEntity>().find().asFlow()

                    employees.collectAndSend(
                        transform = { it.toExternalModel() },
                        send = { send(it) }
                    )
                }
            } catch (e: Exception) {
                send(emptyList())
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

    override fun getAllPayments(searchText: String): Flow<List<Payment>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val salaries = realm
                        .query<PaymentEntity>()
                        .sort("paymentDate", Sort.DESCENDING)
                        .find()
                        .asFlow()

                    salaries.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterEmployeeSalary(searchText) },
                        send = { send(it) }
                    )
                } catch (e: Exception) {
                    send(emptyList())
                }
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

    override suspend fun getPaymentByEmployeeId(
        employeeId: String,
        selectedDate: Pair<String, String>
    ): Resource<EmployeeSalaryEstimation?> {
        return try {
            val employee =
                realm.query<EmployeeEntity>("employeeId == $0", employeeId).first().find()

            if (employee != null) {
                val employeeSalary = employee.employeeSalary.toLong()
                val perDaySalary = employeeSalary.div(30)

                val payments = realm.query<PaymentEntity>(
                    "employee.employeeId == $0 AND paymentDate >= $1 AND paymentDate <= $2",
                    employeeId,
                    selectedDate.first,
                    selectedDate.second
                ).find()

                val absents = realm.query<AttendanceEntity>(
                    "employee.employeeId == $0 AND absentDate >= $1 AND absentDate <= $2",
                    employeeId,
                    selectedDate.first,
                    selectedDate.second
                ).find()

                var amountPaid: Long = 0
                var noOfPayments: Long = 0
                val noOfAbsents: Long = absents.size.toLong()
                val absentSalary = perDaySalary.times(noOfAbsents)
                val currentSalary = employeeSalary.minus(absentSalary)

                if (payments.isNotEmpty()) {
                    payments.forEach { payment ->
                        amountPaid += payment.paymentAmount.toLong()

                        noOfPayments += 1
                    }
                }

                val status = if (currentSalary >= amountPaid) PaymentStatus.NotPaid else PaymentStatus.Paid

                val message: String? = if (currentSalary < amountPaid) {
                    "Paid Extra ${amountPaid.minus(currentSalary).toString().toRupee} Amount"
                } else if (currentSalary > amountPaid) {
                    "Remaining  ${currentSalary.minus(amountPaid).toString().toRupee} have to pay."
                } else null

                val remainingAmount = currentSalary.minus(amountPaid)

                Resource.Success(
                    EmployeeSalaryEstimation(
                        startDate = selectedDate.first,
                        endDate = selectedDate.second,
                        status = status,
                        message = message,
                        remainingAmount = remainingAmount.toString(),
                        paymentCount = noOfPayments.toString(),
                        absentCount = noOfAbsents.toString(),
                    )
                )
            } else {
                Resource.Error("Unable to find employee")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Salary")
        }
    }

    override suspend fun addNewPayment(newPayment: Payment): Resource<Boolean> {
        return try {
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
                withContext(ioDispatcher) {
                    val employee = realm.query<EmployeeEntity>(
                        "employeeId == $0",
                        newPayment.employee?.employeeId
                    ).first().find()

                    if (employee != null) {
                        val salary = PaymentEntity()
                        salary.paymentId =
                            newPayment.paymentId.ifEmpty { BsonObjectId().toHexString() }
                        salary.paymentAmount = newPayment.paymentAmount
                        salary.paymentMode = newPayment.paymentMode.name
                        salary.paymentDate = newPayment.paymentDate
                        salary.paymentType = newPayment.paymentType.name
                        salary.paymentNote = newPayment.paymentNote
                        salary.createdAt =
                            newPayment.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                        realm.write {
                            findLatest(employee)?.also {
                                salary.employee = it
                            }

                            this.copyToRealm(salary)
                        }

                        Resource.Success(true)
                    } else {
                        Resource.Error("Unable to find employee")
                    }
                }
            } else {
                Resource.Error("Unable to validate employee salary")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error creating Salary Item")
        }
    }

    override suspend fun updatePaymentById(
        newSalary: Payment,
        paymentId: String
    ): Resource<Boolean> {
        return try {
            val validateEmployee = validateEmployee(newSalary.employee?.employeeId ?: "")
            val validateGivenDate = validateGivenDate(newSalary.paymentDate)
            val validatePaymentType = validatePaymentType(newSalary.paymentType)
            val validateSalary = validatePaymentAmount(newSalary.paymentAmount)
            val validateSalaryNote = validatePaymentNote(newSalary.paymentNote)
            val validateSalaryType = validatePaymentMode(newSalary.paymentMode)

            val hasError = listOf(
                validateEmployee,
                validateSalary,
                validateSalaryNote,
                validateSalaryType,
                validatePaymentType,
                validateGivenDate
            ).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val employee = realm.query<EmployeeEntity>(
                        "employeeId == $0",
                        newSalary.employee?.employeeId
                    ).first().find()

                    if (employee != null) {
                        val salary =
                            realm.query<PaymentEntity>("paymentId == $0", paymentId).first()
                                .find()

                        if (salary != null) {
                            realm.write {
                                findLatest(salary)?.apply {
                                    this.paymentAmount = newSalary.paymentAmount
                                    this.paymentMode = newSalary.paymentMode.name
                                    this.paymentDate = newSalary.paymentDate
                                    this.paymentType = newSalary.paymentType.name
                                    this.paymentNote = newSalary.paymentNote
                                    this.updatedAt = System.currentTimeMillis().toString()

                                    findLatest(employee)?.also {
                                        this.employee = it
                                    }
                                }
                            }
                            Resource.Success(true)
                        } else {
                            Resource.Error("Salary not found")
                        }
                    } else {
                        Resource.Error("Unable to find employee")
                    }
                }
            } else {
                Resource.Error("Unable to validate employee salary")
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update employee.")
        }
    }

    override suspend fun deletePaymentById(paymentId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val salary =
                    realm.query<PaymentEntity>("paymentId == $0", paymentId).first().find()

                if (salary != null) {
                    realm.write {
                        findLatest(salary)?.let {
                            delete(it)
                        }
                    }

                    Resource.Success(true)

                } else {
                    Resource.Error("Unable to find salary.")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete salary.")
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

    override suspend fun getEmployeePayments(employeeId: String): Flow<Resource<List<EmployeePayments>>> {
        return channelFlow {
            try {
                val employee =
                    realm.query<EmployeeEntity>("employeeId == $0", employeeId).first().find()

                if (employee != null) {

                    val salary = mutableListOf<EmployeePayments>()

                    val joinedDate = employee.employeeJoinedDate
                    val dates = getSalaryDates(joinedDate)

                    dates.forEach { date ->
                        if (joinedDate <= date.first) {
                            val advancedPayment = mutableListOf<PaymentEntity>()
                            var amountPaid: Long = 0

                            val payments = realm.query<PaymentEntity>(
                                "employee.employeeId == $0 AND paymentDate >= $1 AND paymentDate <= $2",
                                employeeId,
                                date.first,
                                date.second
                            ).find()

                            if (payments.isNotEmpty()) {
                                payments.forEach { payment ->
                                    amountPaid += payment.paymentAmount.toLong()

                                    advancedPayment.add(payment)
                                }
                            }

                            salary.add(
                                EmployeePayments(
                                    startDate = date.first,
                                    endDate = date.second,
                                    payments = advancedPayment.toList().map { it.toExternalModel() }
                                )
                            )
                        }
                    }

                    send(Resource.Success(salary))

                } else {
                    send(Resource.Error("Unable to find employee"))
                }
            } catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get details"))
            }
        }
    }

    override suspend fun getPaymentCalculableDate(employeeId: String): Resource<List<EmployeeMonthlyDate>> {
        return try {
            val employee =
                realm.query<EmployeeEntity>("employeeId == $0", employeeId).first().find()

            if (employee != null) {
                val list = mutableListOf<EmployeeMonthlyDate>()

                val joinedDate = employee.employeeJoinedDate
                val dates = getSalaryDates(joinedDate)

                dates.forEach { date ->
                    if (compareSalaryDates(joinedDate, date.first)) {
                        list.add(
                            EmployeeMonthlyDate(
                                startDate = date.first,
                                endDate = date.second
                            )
                        )
                    }
                }

                Resource.Success(list)
            } else {
                Resource.Error("Unable to find employee")
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Salary Calculable Date")
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