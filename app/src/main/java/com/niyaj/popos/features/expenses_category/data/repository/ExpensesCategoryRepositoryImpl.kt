package com.niyaj.popos.features.expenses_category.data.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.features.expenses_category.domain.repository.ExpensesCategoryRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class ExpensesCategoryRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ExpensesCategoryRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("ExpensesCategoryRealmDao Session")
    }


    override suspend fun getAllExpensesCategory(): Flow<Resource<List<ExpensesCategory>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val categories = realm.query<ExpensesCategory>().sort("expensesCategoryId", Sort.DESCENDING).find()

                    val items = categories.asFlow()

                    items.collect { changes: ResultsChange<ExpensesCategory> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                            is InitialResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                }catch (e: Exception){
                    send(Resource.Error(e.message ?: "Unable to get expanses category items", null))
                }
            }
        }
    }

    override suspend fun getExpensesCategoryById(expensesCategoryId: String): Resource<ExpensesCategory?> {
        return try {
            val expansesCategoryItem = withContext(ioDispatcher) {
                realm.query<ExpensesCategory>("expensesCategoryId == $0", expensesCategoryId).first().find()
            }

            Resource.Success(expansesCategoryItem)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Expanses Category Item", null)
        }
    }

    override suspend fun createNewExpensesCategory(newExpensesCategory: ExpensesCategory): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val expansesCategory = ExpensesCategory()
                expansesCategory.expensesCategoryId = BsonObjectId().toHexString()
                expansesCategory.expensesCategoryName = newExpensesCategory.expensesCategoryName
                expansesCategory.createdAt = System.currentTimeMillis().toString()

                realm.write {
                    this.copyToRealm(expansesCategory)
                }
            }

            Resource.Success(true)
        }catch (e: RealmException){
            Resource.Error(e.message ?: "Error creating expanses category Item")
        }
    }

    override suspend fun updateExpensesCategory(
        newExpensesCategory: ExpensesCategory,
        expensesCategoryId: String,
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val expansesCategory = this.query<ExpensesCategory>("expensesCategoryId == $0", expensesCategoryId).first().find()
                    expansesCategory?.expensesCategoryName = newExpensesCategory.expensesCategoryName
                    expansesCategory?.updatedAt = System.currentTimeMillis().toString()
                }
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update expanses category.")
        }
    }

    override suspend fun deleteExpensesCategory(expensesCategoryId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                realm.write {
                    val expansesCategoryItem: ExpensesCategory = this.query<ExpensesCategory>("expensesCategoryId == $0", expensesCategoryId).find().first()
                    val expenses = this.query<Expenses>("expensesCategory.expensesCategoryId == $0", expensesCategoryId).find()

                    delete(expenses)

                    delete(expansesCategoryItem)
                }
            }

            Resource.Success(true)

        } catch (e: Exception){
            Resource.Error(e.message ?: "Failed to delete expanses category.")
        }
    }
}