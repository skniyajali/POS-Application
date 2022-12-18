package com.niyaj.popos.domain.use_cases.category

import com.niyaj.popos.domain.model.Category
import com.niyaj.popos.domain.repository.CategoryRepository
import com.niyaj.popos.domain.util.Resource

class UpdateCategory(
    private val categoryRepository: CategoryRepository

) {

    suspend operator fun invoke(category: Category, categoryId: String): Resource<Boolean> {
        return categoryRepository.updateCategory(category, categoryId)
    }
}