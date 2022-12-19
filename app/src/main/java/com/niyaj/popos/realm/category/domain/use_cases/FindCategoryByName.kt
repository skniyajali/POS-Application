package com.niyaj.popos.realm.category.domain.use_cases

import com.niyaj.popos.realm.category.domain.repository.CategoryRepository

class FindCategoryByName(
    private val categoryRepository: CategoryRepository
) {

    operator fun invoke(name: String, categoryId: String?): Boolean {
        return categoryRepository.findCategoryByName(name, categoryId)
    }
}