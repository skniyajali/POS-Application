package com.niyaj.popos.realm.category.domain.use_cases

import com.niyaj.popos.realm.category.domain.model.Category
import com.niyaj.popos.realm.category.domain.repository.CategoryRepository
import com.niyaj.popos.domain.util.Resource

class CreateNewCategory(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(category: Category): Resource<Boolean> {
        return categoryRepository.createNewCategory(category)
    }
}