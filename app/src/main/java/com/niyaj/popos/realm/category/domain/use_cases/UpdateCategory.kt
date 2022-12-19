package com.niyaj.popos.realm.category.domain.use_cases

import com.niyaj.popos.realm.category.domain.model.Category
import com.niyaj.popos.realm.category.domain.repository.CategoryRepository
import com.niyaj.popos.domain.util.Resource

class UpdateCategory(
    private val categoryRepository: CategoryRepository

) {

    suspend operator fun invoke(category: Category, categoryId: String): Resource<Boolean> {
        return categoryRepository.updateCategory(category, categoryId)
    }
}