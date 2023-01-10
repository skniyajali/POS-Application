package com.niyaj.popos.features.employee_salary.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class GetAllSalary(private val salaryRepository: SalaryRepository) {

    operator fun invoke(
        searchText: String = "",
    ): Flow<Resource<List<EmployeeSalary>>> {
        return channelFlow {
            withContext(Dispatchers.IO){
                salaryRepository.getAllSalary().collectLatest { result ->
                    when (result){
                        is Resource.Loading -> {
                            send(Resource.Loading(result.isLoading))
                        }
                        is Resource.Success -> {
                            val data = result.data?.let { salaries ->
                                salaries.filter { salary ->
                                    if (searchText.isNotEmpty()){
                                        salary.employeeSalary.contains(searchText, true) ||
                                                salary.salaryType.contains(searchText, true) ||
                                                salary.salaryGivenDate.contains(searchText, true) ||
                                                salary.salaryPaymentType.contains(searchText, true) ||
                                                salary.salaryNote.contains(searchText, true) ||
                                                salary.createdAt.contains(searchText, true) ||
                                                salary.updatedAt?.contains(searchText, true) == true ||
                                                salary.employee?.employeeName?.contains(searchText, true) == true ||
                                                salary.employee?.employeePhone?.contains(searchText, true) == true ||
                                                salary.employee?.employeeType?.contains(searchText, true) == true ||
                                                salary.employee?.employeePosition?.contains(searchText, true) == true
                                    }else{
                                        true
                                    }
                                }
                            }
                            send(Resource.Success(data))
                        }
                        is Resource.Error -> {
                            send(Resource.Error(result.message ?: "Unable to get data from database"))
                        }
                    }
                }
            }
        }
    }

}