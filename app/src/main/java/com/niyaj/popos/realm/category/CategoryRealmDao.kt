package com.niyaj.popos.realm.category

import com.niyaj.popos.domain.model.Category
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CategoryRealmDao {

    suspend fun getAllCategories(): Flow<Resource<List<CategoryRealm>>>

    suspend fun getCategoryById(categoryId: String): Resource<CategoryRealm?>

    fun findCategoryByName(name: String, categoryId: String?): Boolean

    suspend fun createNewCategory(newCategory: Category): Resource<Boolean>

    suspend fun updateCategory(updatedCategory: Category, id: String): Resource<Boolean>

    suspend fun deleteCategory(categoryId: String): Resource<Boolean>
}