package com.niyaj.popos.features.category.domain.use_cases

data class CategoryUseCases(
    val getAllCategories: GetAllCategories,
    val getCategoryById: GetCategoryById,
    val findCategoryByName: FindCategoryByName,
    val createNewCategory: CreateNewCategory,
    val updateCategory: UpdateCategory,
    val deleteCategory: DeleteCategory,
)
