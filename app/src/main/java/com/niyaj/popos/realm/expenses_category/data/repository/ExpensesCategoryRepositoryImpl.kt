package com.niyaj.popos.realm.expenses_category.data.repository

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.expenses.domain.model.Expenses
import com.niyaj.popos.realm.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.realm.expenses_category.domain.repository.ExpensesCategoryRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.isValid
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class ExpensesCategoryRepositoryImpl(config: RealmConfiguration) : ExpensesCategoryRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("ExpensesCategoryRealmDao Session")
    }


    override suspend fun getAllExpensesCategory(): Flow<Resource<List<ExpensesCategory>>> {
        return flow {
            try {
                emit(Resource.Loading(true))

                val items = realm.query<ExpensesCategory>().sort("expensesCategoryId", Sort.DESCENDING).find().asFlow()

                items.collect { changes: ResultsChange<ExpensesCategory> ->
                    when (changes) {
                        is UpdatedResults -> {
                            emit(Resource.Success(changes.list))
                            emit(Resource.Loading(false))
                        }
                        is InitialResults -> {
                            emit(Resource.Success(changes.list))
                            emit(Resource.Loading(false))
                        }
                    }
                }
            }catch (e: Exception){
                Resource.Error(e.message ?: "Unable to get expanses category items", null)
            }
        }
    }

    override suspend fun getExpensesCategoryById(expensesCategoryId: String): Resource<ExpensesCategory?> {
        return try {
            val expansesCategoryItem = realm.query<ExpensesCategory>("expensesCategoryId == $0", expensesCategoryId).first().find()

            Resource.Success(expansesCategoryItem)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Expanses Category Item", null)
        }
    }

    override suspend fun createNewExpensesCategory(newExpensesCategory: ExpensesCategory): Resource<Boolean> {
        return try {
            val expansesCategory = ExpensesCategory()
            expansesCategory.expensesCategoryId = BsonObjectId().toHexString()
            expansesCategory.expensesCategoryName = newExpensesCategory.expensesCategoryName
            expansesCategory.createdAt = System.currentTimeMillis().toString()

            val result = realm.write {
                this.copyToRealm(expansesCategory)
            }

            Resource.Success(result.isValid())
        }catch (e: RealmException){
            Resource.Error(e.message ?: "Error creating expanses category Item")
        }
    }

    override suspend fun updateExpensesCategory(
        newExpensesCategory: ExpensesCategory,
        expensesCategoryId: String,
    ): Resource<Boolean> {
        return try {

            realm.write {
                val expansesCategory = this.query<ExpensesCategory>("expensesCategoryId == $0", expensesCategoryId).first().find()
                expansesCategory?.expensesCategoryName = newExpensesCategory.expensesCategoryName
                expansesCategory?.updatedAt = System.currentTimeMillis().toString()
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update expanses category.")
        }
    }

    override suspend fun deleteExpensesCategory(expensesCategoryId: String): Resource<Boolean> {
        return try {
            realm.write {
                val expansesCategoryItem: ExpensesCategory = this.query<ExpensesCategory>("expensesCategoryId == $0", expensesCategoryId).find().first()
                val expenses = this.query<Expenses>("expansesCategory.expensesCategoryId == $0", expensesCategoryId).find()

                delete(expenses)

                delete(expansesCategoryItem)
            }

            Resource.Success(true)

        } catch (e: Exception){
            Resource.Error(e.message ?: "Failed to delete expanses category.")
        }
    }
}