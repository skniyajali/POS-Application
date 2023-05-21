package com.niyaj.popos.features.employee.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.model.filterEmployee
import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

/**
 * Use case to get all employee data from repository.
 * @constructor [EmployeeRepository] repository to get employee data.
 */
class GetAllEmployee(
    private val employeeRepository: EmployeeRepository
) {
    /**
     * Invoke use case to get all employee data from repository.
     * @param searchText [String] search text to filter employee data.
     * @return [Flow] of [Resource] of [List] of [Employee]
     * @see [Resource]
     * @see [Employee]
     * @see [EmployeeRepository]
     * @see [EmployeeRepository.getAllEmployee]
     */
    suspend operator fun invoke(
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
                                data.filter { employee ->
                                    employee.filterEmployee(searchText = searchText)
                                }
                            }

                            send(Resource.Success(data))
                        }
                        is Resource.Error -> {
                            send(Resource.Error(result.message ?: "Unable to get employee data from repository"))
                        }
                    }
                }
            }
        }
    }
}