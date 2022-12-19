package com.niyaj.popos.domain.util

import com.niyaj.popos.realm.employee.domain.model.Employee
import com.niyaj.popos.domain.model.ExpensesSubCategory

sealed class ExpensesType<T>(val value: T? = null) {

    class EmployeeData(value: Employee?): ExpensesType<Employee>(value)

    class SubCategoryData(value: ExpensesSubCategory?): ExpensesType<ExpensesSubCategory>(value)

}
