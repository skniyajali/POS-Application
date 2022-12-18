package com.niyaj.popos.domain.util.filter_items

import com.niyaj.popos.domain.util.SortType

sealed class FilterExpensesCategory(val sortType: SortType){

    class ByExpensesCategoryId(sortType: SortType): FilterExpensesCategory(sortType)

    class ByExpensesCategoryName(sortType: SortType): FilterExpensesCategory(sortType)

    class ByExpensesCategoryDate(sortType: SortType): FilterExpensesCategory(sortType)


    fun copy(sortType: SortType): FilterExpensesCategory {
        return when(this){
            is ByExpensesCategoryId -> ByExpensesCategoryId(sortType)
            is ByExpensesCategoryName -> ByExpensesCategoryName(sortType)
            is ByExpensesCategoryDate -> ByExpensesCategoryDate(sortType)
        }
    }
}
