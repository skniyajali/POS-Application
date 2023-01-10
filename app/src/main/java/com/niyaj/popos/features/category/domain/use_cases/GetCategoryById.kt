package com.niyaj.popos.features.category.domain.use_cases

import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.category.domain.repository.CategoryRepository
import com.niyaj.popos.features.common.util.Resource

class GetCategoryById(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(categoryId: String): Resource<Category?> {
        return categoryRepository.getCategoryById(categoryId)
    }
}