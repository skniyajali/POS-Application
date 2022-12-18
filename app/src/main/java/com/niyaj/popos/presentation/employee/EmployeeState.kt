package com.niyaj.popos.presentation.employee

import com.niyaj.popos.domain.model.Employee
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterEmployee

data class EmployeeState(
    val employees: List<Employee> = emptyList(),
    val filterEmployee: FilterEmployee = FilterEmployee.ByEmployeeId(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)
