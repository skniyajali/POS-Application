package com.niyaj.popos.features.employee_salary.data.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.features.employee_salary.domain.util.CalculatedSalary
import com.niyaj.popos.features.employee_salary.domain.util.SalaryCalculableDate
import com.niyaj.popos.features.employee_salary.domain.util.SalaryCalculation
import com.niyaj.popos.util.Constants.NOT_PAID
import com.niyaj.popos.util.Constants.PAID
import com.niyaj.popos.util.getSalaryDates
import com.niyaj.popos.util.toRupee
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class SalaryRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SalaryRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Salary Session")
    }


    override fun getAllSalary(): Flow<Resource<List<EmployeeSalary>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val salaries = realm.query<EmployeeSalary>().sort("salaryGivenDate", Sort.DESCENDING).find()

                    val items = salaries.asFlow()

                    items.collect { changes: ResultsChange<EmployeeSalary> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                            is InitialResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                }catch (e: Exception){
                    send(Resource.Error(e.message ?: "Unable to get salary items", emptyList()))
                }
            }
        }
    }

    override suspend fun getSalaryById(salaryId: String): Resource<EmployeeSalary?> {
        return try {
            val salary = withContext(ioDispatcher) {
                realm.query<EmployeeSalary>("salaryId == $0", salaryId).first().find()
            }

            Resource.Success(salary)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Salary", null)
        }
    }

    override suspend fun getSalaryByEmployeeId(
        employeeId: String,
        selectedDate: Pair<String, String>
    ): Resource<CalculatedSalary?> {
        return try {
            val employee = realm.query<Employee>("employeeId == $0", employeeId).first().find()

            if (employee != null) {
                val employeeSalary = employee.employeeSalary.toLong()
                val perDaySalary = employeeSalary.div(30)

                val payments = realm.query<EmployeeSalary>(
                    "employee.employeeId == $0 AND salaryGivenDate >= $1 AND salaryGivenDate <= $2",
                    employeeId,
                    selectedDate.first,
                    selectedDate.second
                ).find()

                val absents = realm.query<EmployeeAttendance>(
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
                        amountPaid += payment.employeeSalary.toLong()

                        noOfPayments += 1
                    }
                }

                val status = if(currentSalary >= amountPaid) NOT_PAID else PAID

                val message: String? = if (currentSalary < amountPaid) {
                    "Paid Extra ${amountPaid.minus(currentSalary).toString().toRupee} Amount"
                } else if(currentSalary > amountPaid) {
                    "Remaining  ${currentSalary.minus(amountPaid).toString().toRupee} have to pay."
                } else null

                val remainingAmount = currentSalary.minus(amountPaid)

                Resource.Success(
                    CalculatedSalary(
                        startDate = selectedDate.first,
                        endDate = selectedDate.second,
                        status = status,
                        message = message,
                        remainingAmount = remainingAmount.toString(),
                        paymentCount = noOfPayments.toString(),
                        absentCount = noOfAbsents.toString(),
                    )
                )
            }else {
                Resource.Error("Unable to find employee", null)
            }
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Salary", null)
        }
    }

    override suspend fun addNewSalary(newSalary: EmployeeSalary): Resource<Boolean> {
        return try {
            val employee = realm.query<Employee>("employeeId == $0", newSalary.employee?.employeeId).first().find()

            if (employee != null) {
                withContext(ioDispatcher){
                    val salary = EmployeeSalary()
                    salary.salaryId = BsonObjectId().toHexString()
                    salary.employeeSalary = newSalary.employeeSalary
                    salary.salaryType = newSalary.salaryType
                    salary.salaryGivenDate = newSalary.salaryGivenDate
                    salary.salaryPaymentType = newSalary.salaryPaymentType
                    salary.salaryNote = newSalary.salaryNote
                    salary.createdAt = System.currentTimeMillis().toString()

                    realm.write {
                        findLatest(employee)?.also {
                            salary.employee = it
                        }

                        this.copyToRealm(salary)
                    }
                }

                Resource.Success(true)
            }else {
                Resource.Error("Unable to find employee", false)
            }

        }catch (e: Exception){
            Resource.Error(e.message ?: "Error creating Salary Item", false)
        }
    }

    override suspend fun updateSalaryById(salaryId: String, newSalary: EmployeeSalary): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val employee = this.query<Employee>("employeeId == $0", newSalary.employee?.employeeId).first().find()

                    if (employee != null) {
                        val salary = this.query<EmployeeSalary>("salaryId == $0", salaryId).first().find()
                        salary?.employeeSalary = newSalary.employeeSalary
                        salary?.salaryType = newSalary.salaryType
                        salary?.salaryGivenDate = newSalary.salaryGivenDate
                        salary?.salaryPaymentType = newSalary.salaryPaymentType
                        salary?.salaryNote = newSalary.salaryNote
                        salary?.employee = employee
                        salary?.updatedAt = System.currentTimeMillis().toString()
                    } else {
                        Resource.Error("Unable to find employee", false)
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update employee.", false)
        }
    }

    override suspend fun deleteSalaryById(salaryId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val salary = this.query<EmployeeSalary>("salaryId == $0", salaryId).first().find()
                    if (salary != null) {
                        delete(salary)
                    }else {
                        Resource.Error("Unable to find salary.", false)
                    }
                }
            }
            Resource.Success(true)
        }catch (e:Exception) {
            Resource.Error(e.message ?: "Unable to delete salary.", false)
        }
    }

    override suspend fun getEmployeeSalary(employeeId: String): Flow<Resource<List<SalaryCalculation>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))
                val employee = realm.query<Employee>("employeeId == $0", employeeId).first().find()

                if (employee != null) {

                    val salary = mutableListOf<SalaryCalculation>()

                    val joinedDate = employee.employeeJoinedDate
                    val dates = getSalaryDates(joinedDate)

                    dates.forEach { date ->
                        if (joinedDate <= date.first) {
                            val advancedPayment = mutableListOf<EmployeeSalary>()
                            var amountPaid: Long = 0
                            val employeeSalary = employee.employeeSalary.toLong()

                            val payments = realm.query<EmployeeSalary>("employee.employeeId == $0 AND salaryGivenDate >= $1 AND salaryGivenDate <= $2", employeeId, date.first, date.second).find()

                            if (payments.isNotEmpty()) {
                                payments.forEach { payment ->
                                    amountPaid += payment.employeeSalary.toLong()

                                    advancedPayment.add(payment)
                                }
                            }

                            val status = if(employeeSalary >= amountPaid) NOT_PAID else PAID

                            val message: String? = if (employeeSalary < amountPaid) {
                                "Paid Extra ${amountPaid.minus(employeeSalary).toString().toRupee} Amount"
                            } else if(employeeSalary > amountPaid) {
                                "Remaining  ${employeeSalary.minus(amountPaid).toString().toRupee} have to pay."
                            } else null

                            salary.add(
                                SalaryCalculation(
                                    startDate = date.first,
                                    endDate = date.second,
                                    status = status,
                                    message = message,
                                    payments = advancedPayment.toList()
                                )
                            )
                        }
                    }

                    send(Resource.Success(salary.toList()))
                    send(Resource.Loading(false))

                }else {
                    send(Resource.Error("Unable to find employee", emptyList()))
                }
            }catch (e:Exception) {
                send(Resource.Error(e.message ?: "Unable to get details", emptyList()))
            }
        }
    }

    override suspend fun getSalaryCalculableDate(employeeId: String): Resource<List<SalaryCalculableDate>> {
        return try {
            val employee = realm.query<Employee>("employeeId == $0", employeeId).first().find()

            if (employee != null) {
                val list = mutableListOf<SalaryCalculableDate>()

                val joinedDate = employee.employeeJoinedDate
                val dates = getSalaryDates(joinedDate)

                dates.forEach { date ->
                    if (joinedDate <= date.first) {
                        list.add(
                            SalaryCalculableDate(
                                startDate = date.first,
                                endDate = date.second
                            )
                        )
                    }
                }

                Resource.Success(list)
            }else {
                Resource.Error("Unable to find employee", emptyList())
            }

        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Salary Calculable Date", emptyList())
        }
    }
}