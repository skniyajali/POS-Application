package com.niyaj.data.data.repository

import com.niyaj.common.tags.CategoryTestTags.CATEGORY_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.CategoryTestTags.CATEGORY_NAME_EMPTY_ERROR
import com.niyaj.common.tags.CategoryTestTags.CATEGORY_NAME_LENGTH_ERROR
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
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
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
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
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val items = realm.query<CategoryEntity>()
                        .sort("categoryId", Sort.ASCENDING)
                        .find()
                        .asFlow()

                    items.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterCategory(searchText) },
                        send = { send(it) }
                    )

                } catch (e: Exception) {
                    send(emptyList())
                }
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

    override fun findCategoryByName(name: String, categoryId: String?): Boolean {
        val category = if (categoryId.isNullOrEmpty()) {
            realm.query<CategoryEntity>("categoryName == $0", name).first().find()
        } else {
            realm.query<CategoryEntity>("categoryId != $0 && categoryName == $1", categoryId, name)
                .first()
                .find()
        }

        return category != null
    }

    override suspend fun createNewCategory(newCategory: Category): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateCategoryName = validateCategoryName(newCategory.categoryName)

                if (validateCategoryName.successful) {
                    val category = CategoryEntity()
                    category.categoryId =
                        newCategory.categoryId.ifEmpty { BsonObjectId().toHexString() }
                    category.categoryName = newCategory.categoryName
                    category.categoryAvailability = newCategory.categoryAvailability
                    category.createdAt =
                        newCategory.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        this.copyToRealm(category)
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error(
                        validateCategoryName.errorMessage ?: "Unable to create category"
                    )
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create new category")
        }
    }

    override suspend fun updateCategory(
        updatedCategory: Category,
        categoryId: String
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateCategoryName =
                    validateCategoryName(updatedCategory.categoryName, categoryId)

                if (validateCategoryName.successful) {
                    val category =
                        realm.query<CategoryEntity>("categoryId == $0", categoryId).first().find()

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
                        Resource.Error(
                            validateCategoryName.errorMessage ?: "Unable to update category"
                        )
                    }
                } else {
                    Resource.Error(
                        validateCategoryName.errorMessage ?: "Unable to update category"
                    )
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update category")
        }
    }

    override suspend fun deleteCategory(categoryId: String): Resource<Boolean> {
        return try {
            val category =
                realm.query<CategoryEntity>("categoryId == $0", categoryId).first().find()

            if (category != null) {
                withContext(ioDispatcher) {
                    realm.write {
                        val products =
                            this.query<ProductEntity>("category.categoryId == $0", categoryId)
                                .find()
                        val cartOrders =
                            this.query<CartEntity>("product.category.categoryId == $0", categoryId)
                                .find()

                        delete(cartOrders)

                        delete(products)

                        findLatest(category)?.let {
                            delete(it)
                        }
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Unable to find category")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete category")
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

    override fun validateCategoryName(categoryName: String, categoryId: String?): ValidationResult {
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

        if (this.findCategoryByName(categoryName, categoryId)) {
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