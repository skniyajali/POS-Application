package com.niyaj.popos.realm.expenses_category

import com.niyaj.popos.domain.model.ExpensesCategory
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.expenses.ExpensesRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.isValid
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class ExpensesCategoryRealmDaoImpl(config: RealmConfiguration) : ExpensesCategoryRealmDao {

    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        Timber.d("ExpensesCategoryRealmDao Session: $sessionState")
    }


    override suspend fun getAllExpansesCategory(): Flow<Resource<List<ExpensesCategoryRealm>>> {
        return flow {
            try {
                emit(Resource.Loading(true))

                val items = realm.query<ExpensesCategoryRealm>().sort("_id", Sort.DESCENDING).find().asFlow()

                items.collect { changes: ResultsChange<ExpensesCategoryRealm> ->
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

    override suspend fun getExpansesCategoryById(expensesCategoryId: String): Resource<ExpensesCategoryRealm?> {
        return try {
            val expansesCategoryItem = realm.query<ExpensesCategoryRealm>("_id == $0", expensesCategoryId).first().find()

            Resource.Success(expansesCategoryItem)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Expanses Category Item", null)
        }
    }

    override suspend fun createNewExpansesCategory(newExpensesCategory: ExpensesCategory): Resource<Boolean> {
        return try {
            val expansesCategory = ExpensesCategoryRealm()
            expansesCategory.expansesCategoryName = newExpensesCategory.expensesCategoryName

            val result = realm.write {
                this.copyToRealm(expansesCategory)
            }

            Resource.Success(result.isValid())
        }catch (e: RealmException){
            Resource.Error(e.message ?: "Error creating expanses category Item")
        }
    }

    override suspend fun updateExpansesCategory(
        newExpensesCategory: ExpensesCategory,
        expensesCategoryId: String,
    ): Resource<Boolean> {
        return try {

            realm.write {
                val expansesCategory = this.query<ExpensesCategoryRealm>("_id == $0", expensesCategoryId).first().find()
                expansesCategory?.expansesCategoryName = newExpensesCategory.expensesCategoryName
                expansesCategory?.updated_at = System.currentTimeMillis().toString()

            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update expanses category.")
        }
    }

    override suspend fun deleteExpansesCategory(expensesCategoryId: String): Resource<Boolean> {
        return try {
            realm.write {
                val expansesCategoryItem: ExpensesCategoryRealm = this.query<ExpensesCategoryRealm>("_id == $0", expensesCategoryId).find().first()
                val expenses = this.query<ExpensesRealm>("expansesCategory._id == $0", expensesCategoryId).find()

                delete(expenses)

                delete(expansesCategoryItem)
            }

            Resource.Success(true)

        } catch (e: Exception){
            Resource.Error(e.message ?: "Failed to delete expanses category.")
        }
    }
}