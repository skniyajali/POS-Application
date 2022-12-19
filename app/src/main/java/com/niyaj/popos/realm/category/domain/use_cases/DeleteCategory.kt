package com.niyaj.popos.realm.category.domain.use_cases

import com.niyaj.popos.realm.category.domain.repository.CategoryRepository
import com.niyaj.popos.domain.util.Resource

class DeleteCategory(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(categoryId: String): Resource<Boolean> {
        return categoryRepository.deleteCategory(categoryId)
    }
}