package com.niyaj.popos.domain.use_cases.category

import com.niyaj.popos.domain.repository.CategoryRepository
import com.niyaj.popos.domain.util.Resource

class DeleteCategory(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(categoryId: String): Resource<Boolean> {
        return categoryRepository.deleteCategory(categoryId)
    }
}