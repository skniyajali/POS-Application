package com.niyaj.popos.domain.use_cases.category

import com.niyaj.popos.domain.repository.CategoryRepository

class FindCategoryByName(
    private val categoryRepository: CategoryRepository
) {

    operator fun invoke(name: String, categoryId: String?): Boolean {
        return categoryRepository.findCategoryByName(name, categoryId)
    }
}