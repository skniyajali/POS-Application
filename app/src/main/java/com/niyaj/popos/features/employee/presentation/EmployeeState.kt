package com.niyaj.popos.features.employee.presentation

import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.util.FilterEmployee

data class EmployeeState(
    val employees: List<Employee> = emptyList(),
    val filterEmployee: FilterEmployee = FilterEmployee.ByEmployeeId(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)
