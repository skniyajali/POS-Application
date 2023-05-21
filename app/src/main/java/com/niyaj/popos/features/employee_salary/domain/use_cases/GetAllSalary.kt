package com.niyaj.popos.features.employee_salary.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.employee_salary.domain.model.filterEmployeeSalary
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

/**
 * Get all employees salary
 * @constructor SalaryRepository
 * @see invoke
 */
class GetAllSalary(private val salaryRepository: SalaryRepository) {
    /**
     * 
     */
    operator fun invoke(searchText : String = ""): Flow<Resource<List<EmployeeSalary>>> {
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
                                    salary.filterEmployeeSalary(searchText)
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