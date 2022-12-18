package com.niyaj.popos.domain.util.filter_items

import com.niyaj.popos.domain.util.SortType

sealed class FilterExpensesSub(val sortType: SortType){

    class ByCategoryIdExpenses(sortType: SortType): FilterExpensesSub(sortType)

    class ByExpensesSubId(sortType: SortType): FilterExpensesSub(sortType)

    class ByExpensesSubName(sortType: SortType): FilterExpensesSub(sortType)

    class ByExpensesSubDesc(sortType: SortType): FilterExpensesSub(sortType)

    class ByExpensesSubDate(sortType: SortType): FilterExpensesSub(sortType)


    fun copy(sortType: SortType): FilterExpensesSub {
        return when(this){
            is ByCategoryIdExpenses -> ByCategoryIdExpenses(sortType)
            is ByExpensesSubId -> ByExpensesSubId(sortType)
            is ByExpensesSubName -> ByExpensesSubName(sortType)
            is ByExpensesSubDesc -> ByExpensesSubDesc(sortType)
            is ByExpensesSubDate -> ByExpensesSubDate(sortType)
        }
    }
}
