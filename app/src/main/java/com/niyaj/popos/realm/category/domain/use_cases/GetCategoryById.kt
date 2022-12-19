package com.niyaj.popos.realm.category.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.category.domain.model.Category
import com.niyaj.popos.realm.category.domain.repository.CategoryRepository

class GetCategoryById(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(categoryId: String): Resource<Category?> {
        return categoryRepository.getCategoryById(categoryId)
    }
}