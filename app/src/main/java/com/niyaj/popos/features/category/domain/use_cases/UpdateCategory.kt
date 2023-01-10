package com.niyaj.popos.features.category.domain.use_cases

import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.category.domain.repository.CategoryRepository
import com.niyaj.popos.features.common.util.Resource

class UpdateCategory(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category, categoryId: String): Resource<Boolean> {
        return categoryRepository.updateCategory(category, categoryId)
    }
}