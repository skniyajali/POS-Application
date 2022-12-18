package com.niyaj.popos.realm.category

import com.niyaj.popos.domain.model.Category
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.cart.CartRealm
import com.niyaj.popos.realm.product.ProductRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import timber.log.Timber

class CategoryRealmDaoImpl(
    config: RealmConfiguration
) : CategoryRealmDao {

    val realm = Realm.open(config)

    init {
        Timber.d("Category Session:")
    }

    override suspend fun getAllCategories(): Flow<Resource<List<CategoryRealm>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val items: RealmResults<CategoryRealm> =
                    realm.query<CategoryRealm>().sort("_id", Sort.DESCENDING).find()
                // create a Flow from the Item collection, then add a listener to the Flow
                val itemsFlow = items.asFlow()
                itemsFlow.collect { changes: ResultsChange<CategoryRealm> ->
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

    override suspend fun getCategoryById(categoryId: String): Resource<CategoryRealm?> {
        return try {
            val category = realm.query<CategoryRealm>("_id == $0", categoryId).first().find()
            Resource.Success(category)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get category", null)
        }
    }

    override fun findCategoryByName(name: String, categoryId: String?): Boolean {
        val category = if (categoryId == null) {
            realm.query<CategoryRealm>("categoryName == $0", name).first().find()
        } else {
            realm.query<CategoryRealm>("_id != $0 && categoryName == $1", categoryId, name).first()
                .find()
        }

        return category != null
    }

    override suspend fun createNewCategory(newCategory: Category): Resource<Boolean> {
        return try {
            val category = CategoryRealm()
            category.categoryName = newCategory.categoryName
            category.categoryAvailability = newCategory.categoryAvailability

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
                val category = this.query<CategoryRealm>("_id == $0", id).first().find()
                category?.categoryName = updatedCategory.categoryName
                category?.categoryAvailability = updatedCategory.categoryAvailability
                category?.updated_at = System.currentTimeMillis().toString()
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update category", false)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Resource<Boolean> {
        return try {
            realm.write {
                val category: CategoryRealm =
                    this.query<CategoryRealm>("_id == $0", categoryId).find().first()
                val products: RealmResults<ProductRealm> =
                    this.query<ProductRealm>("category._id == $0", categoryId).find()
                val cartOrders =
                    this.query<CartRealm>("product.category._id == $0", categoryId).find()

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