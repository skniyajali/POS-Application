package com.niyaj.popos.features.category.data.repository

import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.category.domain.repository.CategoryRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.product.domain.model.Product
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class CategoryRepositoryImpl(
    config: RealmConfiguration
) : CategoryRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Category Session:")
    }

    override suspend fun getAllCategories(): Flow<Resource<List<Category>>> {
        return channelFlow {
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
                send(Resource.Error(e.message ?: "Unable to get all categories"))
            }
        }
    }

    override suspend fun getCategoryById(categoryId: String): Resource<Category?> {
        return try {
            val category = realm.query<Category>("categoryId == $0", categoryId).first().find()
            Resource.Success(category)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get category", null)
        }
    }

    override fun findCategoryByName(name: String, categoryId: String?): Boolean {
        val category = if (categoryId == null) {
            realm.query<Category>("categoryName == $0", name).first().find()
        } else {
            realm.query<Category>("categoryId != $0 && categoryName == $1", categoryId, name).first()
                .find()
        }

        return category != null
    }

    override suspend fun createNewCategory(newCategory: Category): Resource<Boolean> {
        return try {
            val category = Category()
            category.categoryId = BsonObjectId().toHexString()
            category.categoryName = newCategory.categoryName
            category.categoryAvailability = newCategory.categoryAvailability
            category.createdAt = System.currentTimeMillis().toString()

            realm.write {
                this.copyToRealm(category)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create new category", false)
        }
    }

    override suspend fun updateCategory(updatedCategory: Category, id: String): Resource<Boolean> {
        return try {
            realm.write {
                val category = this.query<Category>("categoryId == $0", id).first().find()
                category?.categoryName = updatedCategory.categoryName
                category?.categoryAvailability = updatedCategory.categoryAvailability
                category?.updatedAt = System.currentTimeMillis().toString()
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update category", false)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Resource<Boolean> {
        return try {
            realm.write {
                val category: Category =
                    this.query<Category>("categoryId == $0", categoryId).find().first()
                val products: RealmResults<Product> =
                    this.query<Product>("category.categoryId == $0", categoryId).find()
                val cartOrders =
                    this.query<CartRealm>("product.category.categoryId == $0", categoryId).find()

                delete(cartOrders)

                delete(products)

                delete(category)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete category", false)
        }
    }

}