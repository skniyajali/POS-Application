package com.niyaj.data.data.repository

import com.niyaj.common.tags.CategoryTestTags.CATEGORY_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.CategoryTestTags.CATEGORY_NAME_EMPTY_ERROR
import com.niyaj.common.tags.CategoryTestTags.CATEGORY_NAME_LENGTH_ERROR
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.data.repository.validation.CategoryValidationRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CategoryEntity
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Category
import com.niyaj.model.filterCategory
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber

class CategoryRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher,
) : CategoryRepository, CategoryValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Category Session:")
    }

    override suspend fun getAllCategories(searchText: String): Flow<List<Category>> {
        return withContext(ioDispatcher) {
            realm.query<CategoryEntity>()
                .sort("categoryId", Sort.ASCENDING)
                .find()
                .asFlow()
                .mapLatest { items ->
                    items.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterCategory(searchText) },
                    )
                }
        }
    }

    override suspend fun getCategoryById(categoryId: String): Resource<Category?> {
        return try {
            val category = withContext(ioDispatcher) {
                realm.query<CategoryEntity>("categoryId == $0", categoryId).first().find()
            }
            Resource.Success(category?.toExternalModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get category")
        }
    }

    override suspend fun findCategoryByName(name: String, categoryId: String?): Boolean {
        return withContext(ioDispatcher) {
            if (categoryId.isNullOrEmpty()) {
                realm.query<CategoryEntity>("categoryName == $0", name).first().find()
            } else {
                realm.query<CategoryEntity>(
                    "categoryId != $0 && categoryName == $1",
                    categoryId,
                    name
                )
                    .first()
                    .find()
            } != null
        }
    }

    override suspend fun createOrUpdateCategory(
        updatedCategory: Category,
        categoryId: String
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateName = validateCategoryName(updatedCategory.categoryName, categoryId)

                if (validateName.successful) {
                    val category = realm.query<CategoryEntity>("categoryId == $0", categoryId)
                        .first().find()

                    if (category != null) {
                        realm.write {
                            findLatest(category)?.apply {
                                this.categoryName = updatedCategory.categoryName
                                this.categoryAvailability = updatedCategory.categoryAvailability
                                this.updatedAt = System.currentTimeMillis().toString()
                            }
                        }

                        Resource.Success(true)
                    } else {
                        realm.write {
                            this.copyToRealm(updatedCategory.toEntity())
                        }

                        Resource.Success(true)
                    }
                } else {
                    Resource.Error(validateName.errorMessage ?: "Unable")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update category")
        }
    }

    override suspend fun deleteCategories(categoryIds: List<String>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                categoryIds.forEach { categoryId ->
                    val category = realm
                        .query<CategoryEntity>("categoryId == $0", categoryId)
                        .first()
                        .find()

                    if (category != null) {
                        withContext(ioDispatcher) {
                            realm.write {
                                val products = this
                                    .query<ProductEntity>(
                                        "category.categoryId == $0",
                                        categoryId
                                    ).find()

                                val cartOrders =
                                    this.query<CartEntity>(
                                        "product.category.categoryId == $0",
                                        categoryId
                                    ).find()

                                delete(cartOrders)

                                delete(products)

                                findLatest(category)?.let {
                                    delete(it)
                                }
                            }
                        }
                    }
                }

                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete categories")
        }
    }

    override suspend fun validateCategoryName(
        categoryName: String,
        categoryId: String?
    ): ValidationResult {
        if (categoryName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = CATEGORY_NAME_EMPTY_ERROR
            )
        }

        if (categoryName.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = CATEGORY_NAME_LENGTH_ERROR
            )
        }

        val result = withContext(ioDispatcher) {
            findCategoryByName(categoryName, categoryId)
        }

        if (result) {
            return ValidationResult(
                successful = false,
                errorMessage = CATEGORY_NAME_ALREADY_EXIST_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}