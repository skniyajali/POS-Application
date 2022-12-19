package com.niyaj.popos.realm.category.domain.repository

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.category.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun getAllCategories(): Flow<Resource<List<com.niyaj.popos.realm.category.domain.model.Category>>>

    suspend fun getCategoryById(categoryId: String): Resource<com.niyaj.popos.realm.category.domain.model.Category?>

    fun findCategoryByName(name: String, categoryId: String?): Boolean

    suspend fun createNewCategory(newCategory: Category): Resource<Boolean>

    suspend fun updateCategory(updatedCategory: Category, id: String): Resource<Boolean>

    suspend fun deleteCategory(categoryId: String): Resource<Boolean>
}