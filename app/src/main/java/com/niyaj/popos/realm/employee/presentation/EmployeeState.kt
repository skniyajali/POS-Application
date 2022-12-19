package com.niyaj.popos.realm.employee.presentation

import com.niyaj.popos.realm.employee.domain.model.Employee
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.employee.domain.util.FilterEmployee

data class EmployeeState(
    val employees: List<Employee> = emptyList(),
    val filterEmployee: FilterEmployee = FilterEmployee.ByEmployeeId(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)
