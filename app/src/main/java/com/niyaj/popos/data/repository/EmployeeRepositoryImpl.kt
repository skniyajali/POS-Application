package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.Employee
import com.niyaj.popos.domain.repository.EmployeeRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee.EmployeeRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EmployeeRepositoryImpl(
    private val employeeRealmDao: EmployeeRealmDao
) : EmployeeRepository {
    override suspend fun getAllEmployee(): Flow<Resource<List<Employee>>> {
        return flow {
            employeeRealmDao.getAllEmployee().collect{ result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.map { employee ->
                                Employee(
                                    employeeId = employee._id,
                                    employeeName = employee.employeeName,
                                    employeePhone = employee.employeePhone,
                                    employeeSalary = employee.employeeSalary,
                                    employeeSalaryType = employee.employeeSalaryType,
                                    employeePosition = employee.employeePosition,
                                    employeeType = employee.employeeType,
                                    employeeJoinedDate = employee.employeeJoinedDate,
                                    createdAt = employee.created_at,
                                    updatedAt = employee.updated_at
                                )
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get employees from database"))
                    }
                }
            }
        }
    }

    override suspend fun getEmployeeById(employeeId: String): Resource<Employee?> {
        val result = employeeRealmDao.getEmployeeById(employeeId)

        return result.data?.let { employee ->
            Resource.Success(
                data = Employee(
                    employeeId = employee._id,
                    employeeName = employee.employeeName,
                    employeePhone = employee.employeePhone,
                    employeeSalary = employee.employeeSalary,
                    employeeSalaryType = employee.employeeSalaryType,
                    employeePosition = employee.employeePosition,
                    employeeType = employee.employeeType,
                    employeeJoinedDate = employee.employeeJoinedDate,
                    createdAt = employee.created_at,
                    updatedAt = employee.updated_at
                )
            )
        } ?: Resource.Error(result.message ?: "Could not get employee")
    }

    override fun findEmployeeByName(employeeName: String, employeeId: String?): Boolean {
        return employeeRealmDao.findEmployeeByName(employeeName, employeeId)
    }

    override fun findEmployeeByPhone(employeePhone: String, employeeId: String?): Boolean {
        return employeeRealmDao.findEmployeeByPhone(employeePhone, employeeId)
    }

    override suspend fun createNewEmployee(newEmployee: Employee): Resource<Boolean> {
        return employeeRealmDao.createNewEmployee(newEmployee)
    }

    override suspend fun updateEmployee(newEmployee: Employee, employeeId: String): Resource<Boolean> {
        return employeeRealmDao.updateEmployee(newEmployee, employeeId)
    }

    override suspend fun deleteEmployee(employeeId: String): Resource<Boolean> {
        return employeeRealmDao.deleteEmployee(employeeId)
    }
}