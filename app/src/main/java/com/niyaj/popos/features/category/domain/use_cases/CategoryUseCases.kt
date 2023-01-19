package com.niyaj.popos.features.category.domain.use_cases

import com.niyaj.popos.features.category.domain.use_cases.validation.ValidateCategoryName

data class CategoryUseCases(
    val validateCategoryName: ValidateCategoryName,
    val getAllCategories: GetAllCategories,
    val getCategoryById: GetCategoryById,
    val createNewCategory: CreateNewCategory,
    val updateCategory: UpdateCategory,
    val deleteCategory: DeleteCategory,
)
