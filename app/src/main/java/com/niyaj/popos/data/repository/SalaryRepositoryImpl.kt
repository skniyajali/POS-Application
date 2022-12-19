package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.*
import com.niyaj.popos.domain.repository.SalaryRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee.domain.model.Employee
import com.niyaj.popos.realm.employee_salary.SalaryRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class SalaryRepositoryImpl(
    private val salaryRealmDao: SalaryRealmDao
) : SalaryRepository {

    override fun getAllSalary(): Flow<Resource<List<EmployeeSalary>>> {
        return channelFlow {
            salaryRealmDao.getAllSalary().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let { data ->
                            data.map { salary ->
                                EmployeeSalary(
                                    salaryId = salary._id,
                                    salaryType = salary.salaryType,
                                    employeeSalary = salary.employeeSalary,
                                    salaryGivenDate = salary.salaryGivenDate,
                                    createdAt = salary.created_at,
                                    updatedAt = salary.updated_at,
                                    salaryPaymentType = salary.salaryPaymentType,
                                    salaryNote = salary.salaryNote,
                                    employee = if(salary.employee != null) Employee(
                                        employeeId = salary.employee!!.employeeId,
                                        employeeName = salary.employee!!.employeeName,
                                        employeePhone = salary.employee!!.employeePhone,
                                        employeeSalary = salary.employee!!.employeeSalary,
                                        employeeSalaryType = salary.employee!!.employeeSalaryType,
                                        employeePosition = salary.employee!!.employeePosition,
                                        employeeType = salary.employee!!.employeeType,
                                        employeeJoinedDate = salary.employee!!.employeeJoinedDate,
                                        createdAt = salary.employee!!.createdAt,
                                        updatedAt = salary.employee!!.updatedAt
                                    ) else Employee(),
                                )
                            }
                        }

                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get salary from database"))
                    }
                }
            }
        }
    }

    override fun getSalaryById(salaryId: String): Resource<EmployeeSalary?> {
        val result = salaryRealmDao.getSalaryById(salaryId)

        return result.data?.let { salary ->
            Resource.Success(
                EmployeeSalary(
                    salaryId = salary._id,
                    salaryType = salary.salaryType,
                    employeeSalary = salary.employeeSalary,
                    salaryGivenDate = salary.salaryGivenDate,
                    salaryPaymentType = salary.salaryPaymentType,
                    salaryNote = salary.salaryNote,
                    createdAt = salary.created_at,
                    updatedAt = salary.updated_at,
                    employee = if(salary.employee != null) Employee(
                        employeeId = salary.employee!!.employeeId,
                        employeeName = salary.employee!!.employeeName,
                        employeePhone = salary.employee!!.employeePhone,
                        employeeSalary = salary.employee!!.employeeSalary,
                        employeeSalaryType = salary.employee!!.employeeSalaryType,
                        employeePosition = salary.employee!!.employeePosition,
                        employeeType = salary.employee!!.employeeType,
                        employeeJoinedDate = salary.employee!!.employeeJoinedDate,
                        createdAt = salary.employee!!.createdAt,
                        updatedAt = salary.employee!!.updatedAt
                    ) else Employee(),
                )
            )
        } ?: Resource.Error(
            message = "Unable to get salary by id $salaryId",
            null
        )
    }

    override fun getSalaryByEmployeeId(
        employeeId: String,
        selectedDate: Pair<String, String>
    ): Resource<CalculatedSalary?> {
        return salaryRealmDao.getSalaryByEmployeeId(employeeId, selectedDate)
    }

    override suspend fun addNewSalary(newSalary: EmployeeSalary): Resource<Boolean> {
        return salaryRealmDao.addNewSalary(newSalary)
    }

    override suspend fun updateSalaryById(salaryId: String, newSalary: EmployeeSalary): Resource<Boolean> {
        return salaryRealmDao.updateSalaryById(salaryId, newSalary)
    }

    override suspend fun deleteSalaryById(salaryId: String): Resource<Boolean> {
        return salaryRealmDao.deleteSalaryById(salaryId)
    }

    override fun getEmployeeSalary(employeeId: String): Flow<Resource<List<SalaryCalculation>>> {
        return channelFlow {
            salaryRealmDao.getEmployeeSalary(employeeId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let {
                            it.map { result ->
                                SalaryCalculation(
                                    startDate = result.startDate,
                                    endDate = result.endDate,
                                    status = result.status,
                                    message = result.message,
                                    payments = result.payments.map { salary ->
                                        EmployeeSalary(
                                            salaryId = salary._id,
                                            salaryType = salary.salaryType,
                                            employeeSalary = salary.employeeSalary,
                                            salaryGivenDate = salary.salaryGivenDate,
                                            createdAt = salary.created_at,
                                            updatedAt = salary.updated_at,
                                            salaryPaymentType = salary.salaryPaymentType,
                                            salaryNote = salary.salaryNote,
                                            employee = if(salary.employee != null) Employee(
                                                employeeId = salary.employee!!.employeeId,
                                                employeeName = salary.employee!!.employeeName,
                                                employeePhone = salary.employee!!.employeePhone,
                                                employeeSalary = salary.employee!!.employeeSalary,
                                                employeeSalaryType = salary.employee!!.employeeSalaryType,
                                                employeePosition = salary.employee!!.employeePosition,
                                                employeeType = salary.employee!!.employeeType,
                                                employeeJoinedDate = salary.employee!!.employeeJoinedDate,
                                                createdAt = salary.employee!!.createdAt,
                                                updatedAt = salary.employee!!.updatedAt
                                            ) else Employee(),
                                        )
                                    }
                                )
                            }

                        }
                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to retrieve data from database"))
                    }
                }
            }
        }
    }

    override fun getSalaryCalculableDate(employeeId: String): Resource<List<SalaryCalculableDate>> {
        return salaryRealmDao.getSalaryCalculableDate(employeeId)
    }
}