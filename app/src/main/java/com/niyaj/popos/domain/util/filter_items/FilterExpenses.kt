package com.niyaj.popos.domain.util.filter_items

import com.niyaj.popos.domain.util.SortType

sealed class FilterExpenses(val sortType: SortType){

    class ByExpensesId(sortType: SortType): FilterExpenses(sortType)

    class ByExpensesCategory(sortType: SortType): FilterExpenses(sortType)

    class ByExpensesPrice(sortType: SortType): FilterExpenses(sortType)

    class ByExpensesRemarks(sortType: SortType): FilterExpenses(sortType)

    class ByExpensesDate(sortType: SortType): FilterExpenses(sortType)


    fun copy(sortType: SortType): FilterExpenses {
        return when(this){
            is ByExpensesId -> ByExpensesId(sortType)
            is ByExpensesCategory -> ByExpensesCategory(sortType)
            is ByExpensesPrice -> ByExpensesPrice(sortType)
            is ByExpensesRemarks -> ByExpensesRemarks(sortType)
            is ByExpensesDate -> ByExpensesDate(sortType)
        }
    }
}
