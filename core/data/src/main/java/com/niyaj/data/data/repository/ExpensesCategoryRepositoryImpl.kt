package com.niyaj.data.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.data.repository.ExpensesCategoryRepository
import com.niyaj.data.repository.validation.ExpCategoryValidationRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.ExpensesCategoryEntity
import com.niyaj.database.model.ExpensesEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.ExpensesCategory
import com.niyaj.model.filterExpensesCategory
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class ExpensesCategoryRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher
) : ExpensesCategoryRepository, ExpCategoryValidationRepository {

    val realm = Realm.open(config)

    override suspend fun getAllExpensesCategory(searchText: String): Flow<List<ExpensesCategory>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val categories = realm
                        .query<ExpensesCategoryEntity>()
                        .sort("expensesCategoryId", Sort.DESCENDING)
                        .find()
                        .asFlow()

                    categories.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterExpensesCategory(searchText) },
                        send = { send(it) }
                    )

                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    override suspend fun getExpensesCategoryById(expensesCategoryId: String): Resource<ExpensesCategory?> {
        return try {
            val expansesCategoryItem = withContext(ioDispatcher) {
                realm.query<ExpensesCategoryEntity>("expensesCategoryId == $0", expensesCategoryId)
                    .first().find()
            }

            Resource.Success(expansesCategoryItem?.toExternalModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Expanses Category Item")
        }
    }

    override suspend fun createNewExpensesCategory(newExpensesCategory: ExpensesCategory): Resource<Boolean> {
        return try {
            val validateExpensesCategoryName =
                validateExpensesCategoryName(newExpensesCategory.expensesCategoryName)

            if (validateExpensesCategoryName.successful) {
                withContext(ioDispatcher) {
                    val expansesCategory = ExpensesCategoryEntity()
                    expansesCategory.expensesCategoryId =
                        newExpensesCategory.expensesCategoryId.ifEmpty { BsonObjectId().toHexString() }
                    expansesCategory.expensesCategoryName = newExpensesCategory.expensesCategoryName
                    expansesCategory.createdAt = newExpensesCategory.createdAt.ifEmpty {
                        System.currentTimeMillis().toString()
                    }

                    realm.write {
                        this.copyToRealm(expansesCategory)
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Unable to validate expenses category")
            }
        } catch (e: RealmException) {
            Resource.Error(e.message ?: "Error creating expanses category Item")
        }
    }

    override suspend fun updateExpensesCategory(
        newExpensesCategory: ExpensesCategory,
        expensesCategoryId: String,
    ): Resource<Boolean> {
        return try {
            val validateExpensesCategoryName =
                validateExpensesCategoryName(newExpensesCategory.expensesCategoryName)

            if (validateExpensesCategoryName.successful) {
                withContext(ioDispatcher) {
                    val expansesCategory = realm.query<ExpensesCategoryEntity>(
                        "expensesCategoryId == $0",
                        expensesCategoryId
                    ).first().find()
                    if (expansesCategory != null) {
                        realm.write {
                            findLatest(expansesCategory)?.apply {
                                this.expensesCategoryName = newExpensesCategory.expensesCategoryName
                                this.updatedAt = System.currentTimeMillis().toString()
                            }
                        }

                        Resource.Success(true)
                    } else {
                        Resource.Error("Unable to find expense category")
                    }
                }
            } else {
                Resource.Error("Unable to validate expenses category")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update expanses category.")
        }
    }

    override suspend fun deleteExpensesCategory(expensesCategoryId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val expansesCategoryItem =
                    realm.query<ExpensesCategoryEntity>(
                        "expensesCategoryId == $0",
                        expensesCategoryId
                    )
                        .first().find()

                if (expansesCategoryItem != null) {
                    realm.write {
                        val expenses = this.query<ExpensesEntity>(
                            "expensesCategory.expensesCategoryId == $0",
                            expensesCategoryId
                        ).find()

                        delete(expenses)

                        findLatest(expansesCategoryItem)?.let {
                            delete(it)
                        }
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to find expense category")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete expanses category.")
        }
    }

    override suspend fun deleteExpensesCategories(expensesCategoryIds: List<String>): Resource<Boolean> {
        return try {
            expensesCategoryIds.forEach { expensesCategoryId ->
                withContext(ioDispatcher) {
                    val expansesCategoryItem =
                        realm.query<ExpensesCategoryEntity>(
                            "expensesCategoryId == $0",
                            expensesCategoryId
                        ).first().find()

                    if (expansesCategoryItem != null) {
                        realm.write {
                            val expenses = this.query<ExpensesEntity>(
                                "expensesCategory.expensesCategoryId == $0",
                                expensesCategoryId
                            ).find()

                            delete(expenses)

                            findLatest(expansesCategoryItem)?.let {
                                delete(it)
                            }
                        }
                    }
                }
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete expanses category.")
        }
    }

    override fun validateExpensesCategoryName(categoryName: String): ValidationResult {
        if (categoryName.isEmpty()) return ValidationResult(false, "Category name is empty")

        if (categoryName.length < 3) return ValidationResult(false, "Invalid category name")

        if (categoryName.any { it.isDigit() }) return ValidationResult(
            false,
            "Category name must not contain any digit"
        )

        return ValidationResult(true)
    }
}