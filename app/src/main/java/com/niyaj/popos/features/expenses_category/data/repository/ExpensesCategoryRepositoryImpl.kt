package com.niyaj.popos.features.expenses_category.data.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.features.expenses_category.domain.repository.ExpCategoryValidationRepository
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
) : ExpensesCategoryRepository, ExpCategoryValidationRepository {

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
            val validateExpensesCategoryName = validateExpensesCategoryName(newExpensesCategory.expensesCategoryName)

            if (validateExpensesCategoryName.successful) {
                withContext(ioDispatcher){
                    val expansesCategory = ExpensesCategory()
                    expansesCategory.expensesCategoryId = newExpensesCategory.expensesCategoryId.ifEmpty { BsonObjectId().toHexString() }
                    expansesCategory.expensesCategoryName = newExpensesCategory.expensesCategoryName
                    expansesCategory.createdAt = newExpensesCategory.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        this.copyToRealm(expansesCategory)
                    }
                }

                Resource.Success(true)
            }else {
                Resource.Error("Unable to validate expenses category", false)
            }
        }catch (e: RealmException){
            Resource.Error(e.message ?: "Error creating expanses category Item")
        }
    }

    override suspend fun updateExpensesCategory(
        newExpensesCategory: ExpensesCategory,
        expensesCategoryId: String,
    ): Resource<Boolean> {
        return try {
            val validateExpensesCategoryName = validateExpensesCategoryName(newExpensesCategory.expensesCategoryName)

            if (validateExpensesCategoryName.successful) {
                withContext(ioDispatcher) {
                    val expansesCategory = realm.query<ExpensesCategory>("expensesCategoryId == $0", expensesCategoryId).first().find()
                    if (expansesCategory != null) {
                        realm.write {
                            findLatest(expansesCategory)?.apply {
                                this.expensesCategoryName = newExpensesCategory.expensesCategoryName
                                this.updatedAt = System.currentTimeMillis().toString()
                            }
                        }

                        Resource.Success(true)
                    }else {
                        Resource.Error("Unable to find expense category" ,false)
                    }
                }
            }else {
                Resource.Error("Unable to validate expenses category", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update expanses category.")
        }
    }

    override suspend fun deleteExpensesCategory(expensesCategoryId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val expansesCategoryItem = realm.query<ExpensesCategory>("expensesCategoryId == $0", expensesCategoryId).first().find()

                if (expansesCategoryItem != null) {
                    realm.write {
                        val expenses = this.query<Expenses>("expensesCategory.expensesCategoryId == $0", expensesCategoryId).find()

                        delete(expenses)

                        findLatest(expansesCategoryItem)?.let {
                            delete(it)
                        }
                    }

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to find expense category", false)
                }
            }
        } catch (e: Exception){
            Resource.Error(e.message ?: "Failed to delete expanses category.")
        }
    }

    override fun validateExpensesCategoryName(categoryName: String): ValidationResult {
        if(categoryName.isEmpty()) return ValidationResult(false, "Category name is empty")

        if (categoryName.length < 3) return ValidationResult(false, "Invalid category name")

        if (categoryName.any { it.isDigit() }) return ValidationResult(false, "Category name must not contain any digit")

        return ValidationResult(true)
    }
}