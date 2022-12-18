package com.niyaj.popos.domain.util.filter_items

import com.niyaj.popos.domain.util.SortType

sealed class FilterEmployee(val sortType: SortType){

    class ByEmployeeId(sortType: SortType): FilterEmployee(sortType)

    class ByEmployeeName(sortType: SortType): FilterEmployee(sortType)

    class ByEmployeePhone(sortType: SortType): FilterEmployee(sortType)

    class ByEmployeeSalary(sortType: SortType): FilterEmployee(sortType)

    class ByEmployeeSalaryType(sortType: SortType): FilterEmployee(sortType)

    class ByEmployeePosition(sortType: SortType): FilterEmployee(sortType)

    class ByEmployeeDate(sortType: SortType): FilterEmployee(sortType)


    fun copy(sortType: SortType): FilterEmployee {
        return when(this){
            is ByEmployeeId -> ByEmployeeId(sortType)
            is ByEmployeeName -> ByEmployeeName(sortType)
            is ByEmployeePhone -> ByEmployeePhone(sortType)
            is ByEmployeeSalary -> ByEmployeeSalary(sortType)
            is ByEmployeeSalaryType -> ByEmployeeSalaryType(sortType)
            is ByEmployeePosition -> ByEmployeePosition(sortType)
            is ByEmployeeDate -> ByEmployeeDate(sortType)
        }
    }
}
