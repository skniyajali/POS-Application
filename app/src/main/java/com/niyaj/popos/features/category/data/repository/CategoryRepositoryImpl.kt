package com.niyaj.popos.features.category.data.repository

import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.category.domain.repository.CategoryRepository
import com.niyaj.popos.features.category.domain.repository.CategoryValidationRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.product.domain.model.Product
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class CategoryRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CategoryRepository, CategoryValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Category Session:")
    }

    override suspend fun getAllCategories(): Flow<Resource<List<Category>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val items: RealmResults<Category> =
                        realm.query<Category>().sort("categoryId", Sort.DESCENDING).find()

                    // create a Flow from the Item collection, then add a listener to the Flow
                    val itemsFlow = items.asFlow()
                    itemsFlow.collect { changes: ResultsChange<Category> ->
                        when (changes) {
                            // UpdatedResults means this change represents an update/insert/delete operation
                            is UpdatedResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }

                            else -> {
                                // types other than UpdatedResults are not changes -- ignore them
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                } catch (e: Exception) {
                    send(Resource.Error(e.message ?: "Unable to get all categories", emptyList()))
                }
            }
        }
    }

    override suspend fun getCategoryById(categoryId: String): Resource<Category?> {
        return try {
            val category = withContext(ioDispatcher) {
                realm.query<Category>("categoryId == $0", categoryId).first().find()
            }
            Resource.Success(category)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get category", null)
        }
    }

    override fun findCategoryByName(name: String, categoryId: String?): Boolean {
        val category = if (categoryId.isNullOrEmpty()) {
            realm.query<Category>("categoryName == $0", name).first().find()
        } else {
            realm.query<Category>("categoryId != $0 && categoryName == $1", categoryId, name).first()
                .find()
        }

        return category != null
    }

    override suspend fun createNewCategory(newCategory: Category): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateCategoryName = validateCategoryName(newCategory.categoryName)

                if (validateCategoryName.successful) {
                    val category = Category()
                    category.categoryId = newCategory.categoryId.ifEmpty { BsonObjectId().toHexString() }
                    category.categoryName = newCategory.categoryName
                    category.categoryAvailability = newCategory.categoryAvailability
                    category.createdAt = newCategory.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        this.copyToRealm(category)
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error(validateCategoryName.errorMessage ?: "Unable to create category", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create new category", false)
        }
    }

    override suspend fun updateCategory(updatedCategory: Category, categoryId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val validateCategoryName = validateCategoryName(updatedCategory.categoryName, categoryId)

                if (validateCategoryName.successful) {
                    val category = realm.query<Category>("categoryId == $0", categoryId).first().find()

                    if (category != null) {
                        realm.write {
                            findLatest(category)?.apply {
                                this.categoryName = updatedCategory.categoryName
                                this.categoryAvailability = updatedCategory.categoryAvailability
                                this.updatedAt = System.currentTimeMillis().toString()
                            }
                        }

                        Resource.Success(true)
                    }else {
                        Resource.Error(validateCategoryName.errorMessage ?: "Unable to update category", false)
                    }
                }else {
                    Resource.Error(validateCategoryName.errorMessage ?: "Unable to update category", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update category", false)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Resource<Boolean> {
        return try {
            val category = realm.query<Category>("categoryId == $0", categoryId).first().find()

            if (category != null) {
                withContext(ioDispatcher) {
                    realm.write {
                        findLatest(category)?.let {
                            delete(it)
                        }

                        val products = this.query<Product>("category.categoryId == $0", categoryId).find()
                        val cartOrders = this.query<CartRealm>("product.category.categoryId == $0", categoryId).find()

                        delete(cartOrders)

                        delete(products)
                    }
                }

                Resource.Success(true)
            }else {
                Resource.Error( "Unable to find category", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete category", false)
        }
    }

    override fun validateCategoryName(categoryName: String, categoryId: String?): ValidationResult {
        if(categoryName.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Category name must not be empty"
            )
        }

        if(categoryName.length < 3) {
            return ValidationResult(
                successful = false,
                    errorMessage = "Category name must be 3 characters long"
            )
        }

        if(this.findCategoryByName(categoryName, categoryId)) {
            return ValidationResult(
                successful = false,
                errorMessage = "Category name already exists."
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}