package com.niyaj.popos.features.category.domain.repository

import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun getAllCategories(): Flow<Resource<List<Category>>>

    suspend fun getCategoryById(categoryId: String): Resource<Category?>

    fun findCategoryByName(name: String, categoryId: String?): Boolean

    suspend fun createNewCategory(newCategory: Category): Resource<Boolean>

    suspend fun updateCategory(updatedCategory: Category, id: String): Resource<Boolean>

    suspend fun deleteCategory(categoryId: String): Resource<Boolean>
}