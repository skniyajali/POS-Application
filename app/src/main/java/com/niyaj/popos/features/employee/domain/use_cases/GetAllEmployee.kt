package com.niyaj.popos.features.employee.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.features.employee.domain.util.FilterEmployee
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import timber.log.Timber

class GetAllEmployee(
    private val employeeRepository: EmployeeRepository
) {
    suspend operator fun invoke(
        filterEmployee: FilterEmployee = FilterEmployee.ByEmployeeId(SortType.Descending),
        searchText: String = ""
    ): Flow<Resource<List<Employee>>> {
        return channelFlow {
            withContext(Dispatchers.IO){
                employeeRepository.getAllEmployee().collectLatest { result ->
                    when(result){
                        is Resource.Loading -> {
                            send(Resource.Loading(result.isLoading))
                        }
                        is Resource.Success -> {
                            val data = result.data?.let { data ->
                                when(filterEmployee.sortType){
                                    is SortType.Ascending -> {
                                        when(filterEmployee){
                                            is FilterEmployee.ByEmployeeDate -> { data.sortedBy { it.employeeJoinedDate } }
                                            is FilterEmployee.ByEmployeeId -> { data.sortedBy { it.employeeId } }
                                            is FilterEmployee.ByEmployeeName -> { data.sortedBy { it.employeeName } }
                                            is FilterEmployee.ByEmployeePhone -> { data.sortedBy { it.employeePhone } }
                                            is FilterEmployee.ByEmployeePosition -> { data.sortedBy { it.employeePosition } }
                                            is FilterEmployee.ByEmployeeSalary -> { data.sortedBy { it.employeeSalary } }
                                            is FilterEmployee.ByEmployeeSalaryType -> { data.sortedBy { it.employeeSalaryType } }
                                        }
                                    }
                                    is SortType.Descending -> {
                                        when(filterEmployee){
                                            is FilterEmployee.ByEmployeeDate -> { data.sortedByDescending { it.employeeJoinedDate } }
                                            is FilterEmployee.ByEmployeeId -> { data.sortedByDescending { it.employeeId } }
                                            is FilterEmployee.ByEmployeeName -> { data.sortedByDescending { it.employeeName } }
                                            is FilterEmployee.ByEmployeePhone -> { data.sortedByDescending { it.employeePhone } }
                                            is FilterEmployee.ByEmployeePosition -> { data.sortedByDescending { it.employeePosition } }
                                            is FilterEmployee.ByEmployeeSalary -> { data.sortedByDescending { it.employeeSalary } }
                                            is FilterEmployee.ByEmployeeSalaryType -> { data.sortedByDescending { it.employeeSalaryType } }
                                        }
                                    }
                                }.filter { employee ->
                                    employee.employeeName.contains(searchText, true) ||
                                            employee.employeePosition.contains(searchText, true) ||
                                            employee.employeePhone.contains(searchText, true) ||
                                            employee.employeeSalaryType.contains(searchText, true) ||
                                            employee.employeeSalary.contains(searchText, true) ||
                                            employee.employeeJoinedDate.contains(searchText, true) ||
                                            employee.createdAt.contains(searchText, true) ||
                                            employee.updatedAt?.contains(searchText, true) == true
                                }
                            }

                            send(Resource.Success(data))
                        }
                        is Resource.Error -> {
                            Timber.d("Unable to get employee data from repository")
                            send(Resource.Error(result.message ?: "Unable to get employee data from repository"))
                        }
                    }
                }
            }
        }
    }
}