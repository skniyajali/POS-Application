package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.Category
import com.niyaj.popos.domain.repository.CategoryRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.category.CategoryRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CategoryRepositoryImpl(
    private val categoryRealmDao: CategoryRealmDao
) : CategoryRepository {

    override suspend fun getAllCategories(): Flow<Resource<List<Category>>> {
        return flow {
            categoryRealmDao.getAllCategories().collect { result ->
                when(result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { categories ->
                                categories.map { category ->
                                    Category(
                                        categoryId = category._id,
                                        categoryName = category.categoryName,
                                        categoryAvailability = category.categoryAvailability,
                                        createdAt = category.created_at,
                                        updatedAt = category.updated_at
                                    )
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to load category from database"))
                    }
                }

            }
        }
    }

    override suspend fun getCategoryById(categoryId: String): Resource<Category?> {
        val result = categoryRealmDao.getCategoryById(categoryId)

        return result.data?.let { category ->
            Resource.Success(
                Category(
                    categoryId = category._id,
                    categoryName = category.categoryName,
                    categoryAvailability = category.categoryAvailability,
                    createdAt = category.created_at,
                    updatedAt = category.updated_at
                )
            )
        } ?: Resource.Error(result.message ?: "Unable to get category from database")
    }

    override fun findCategoryByName(name: String, categoryId: String?): Boolean {
        return categoryRealmDao.findCategoryByName(name, categoryId)
    }

    override suspend fun createNewCategory(newCategory: Category): Resource<Boolean> {
        return categoryRealmDao.createNewCategory(newCategory)
    }

    override suspend fun updateCategory(updatedCategory: Category, id: String): Resource<Boolean> {
        return categoryRealmDao.updateCategory(updatedCategory, id)
    }

    override suspend fun deleteCategory(categoryId: String): Resource<Boolean> {
        return categoryRealmDao.deleteCategory(categoryId)
    }
}