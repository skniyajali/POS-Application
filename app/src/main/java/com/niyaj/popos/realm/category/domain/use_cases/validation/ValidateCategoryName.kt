package com.niyaj.popos.realm.category.domain.use_cases.validation

import com.niyaj.popos.realm.category.domain.use_cases.CategoryUseCases
import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateCategoryName @Inject constructor(
    private val categoryUseCases: CategoryUseCases
) {

    fun execute(categoryName: String, categoryId: String?): ValidationResult {

        if(categoryName.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Category name must not be empty"
            )
        }

        if(categoryName.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = "Category name must be 3 characters long"
            )
        }

        if(categoryUseCases.findCategoryByName(categoryName, categoryId)) {
            return ValidationResult(
                successful = false,
                errorMessage = "Category name already exists."
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}