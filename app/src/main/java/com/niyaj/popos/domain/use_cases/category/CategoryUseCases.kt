package com.niyaj.popos.domain.use_cases.category

data class CategoryUseCases(
    val getAllCategories: GetAllCategories,
    val getCategoryById: GetCategoryById,
    val findCategoryByName: FindCategoryByName,
    val createNewCategory: CreateNewCategory,
    val updateCategory: UpdateCategory,
    val deleteCategory: DeleteCategory,
)
