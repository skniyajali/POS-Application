package com.niyaj.popos.domain.use_cases.category

import com.niyaj.popos.domain.model.Category
import com.niyaj.popos.domain.repository.CategoryRepository
import com.niyaj.popos.domain.util.Resource

class CreateNewCategory(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(category: Category): Resource<Boolean> {
        return categoryRepository.createNewCategory(category)
    }
}