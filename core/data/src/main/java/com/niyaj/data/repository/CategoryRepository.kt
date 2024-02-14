package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun getAllCategories(searchText: String): Flow<List<Category>>

    suspend fun getCategoryById(categoryId: String): Resource<Category?>

    fun findCategoryByName(name: String, categoryId: String?): Boolean

    suspend fun createNewCategory(newCategory: Category): Resource<Boolean>

    suspend fun updateCategory(updatedCategory: Category, categoryId: String): Resource<Boolean>

    suspend fun deleteCategory(categoryId: String): Resource<Boolean>

    suspend fun deleteCategories(categoryIds: List<String>): Resource<Boolean>
}