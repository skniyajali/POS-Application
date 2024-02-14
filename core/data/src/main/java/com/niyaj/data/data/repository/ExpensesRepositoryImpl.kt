package com.niyaj.data.data.repository

import com.niyaj.common.tags.ExpenseTestTags.EXPENSES_PRICE_INVALID
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_DATE_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_PRICE_LESS_THAN_TEN_ERROR
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.common.utils.getCalculatedStartDate
import com.niyaj.data.repository.ExpensesRepository
import com.niyaj.data.repository.SettingsRepository
import com.niyaj.data.repository.validation.ExpensesValidationRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.ExpensesCategoryEntity
import com.niyaj.database.model.ExpensesEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Expenses
import com.niyaj.model.filterExpenses
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class ExpensesRepositoryImpl(
    config: RealmConfiguration,
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ExpensesRepository, ExpensesValidationRepository {

    val realm = Realm.open(config)

    override suspend fun getAllExpenses(
        searchText: String,
        expensesDate: String?
    ): Flow<List<Expenses>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val expenses = if (expensesDate != null) {
                        realm.query<ExpensesEntity>("expensesDate == $0", expensesDate)
                            .sort("expensesDate", Sort.DESCENDING)
                            .asFlow()
                    } else {
                        realm.query<ExpensesEntity>()
                            .sort("expensesDate", Sort.DESCENDING)
                            .asFlow()
                    }

                    expenses.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterExpenses(searchText) },
                        send = { send(it) }
                    )

                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    override suspend fun getExpensesById(expensesId: String): Resource<Expenses?> {
        return try {
            val expansesItem = withContext(ioDispatcher) {
                realm.query<ExpensesEntity>("expensesId == $0", expensesId).first().find()
            }

            Resource.Success(expansesItem?.toExternalModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Expanses Item")
        }
    }

    override suspend fun createNewExpenses(newExpenses: Expenses): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateExpensesCategory =
                    validateExpensesCategory(newExpenses.expensesCategory?.expensesCategoryId ?: "")
                val validateExpensesPrice = validateExpensesPrice(newExpenses.expensesAmount)
                val validateExpensesDate = validateExpenseDate(newExpenses.expensesDate)

                val hasError =
                    listOf(
                        validateExpensesCategory,
                        validateExpensesPrice,
                        validateExpensesDate
                    ).any { !it.successful }

                if (!hasError) {
                    val expansesItem = ExpensesEntity()
                    expansesItem.expensesId =
                        newExpenses.expensesId.ifEmpty { BsonObjectId().toHexString() }
                    expansesItem.expensesAmount = newExpenses.expensesAmount
                    expansesItem.expensesDate = newExpenses.expensesDate
                    expansesItem.expensesRemarks = newExpenses.expensesRemarks
                    expansesItem.createdAt =
                        newExpenses.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    val expansesCategory = realm.query<ExpensesCategoryEntity>(
                        "expensesCategoryId == $0",
                        newExpenses.expensesCategory?.expensesCategoryId
                    ).first().find()
                        ?: return@withContext Resource.Error("Unable to find expenses category")

                    realm.write {
                        findLatest(expansesCategory)?.let {
                            expansesItem.expensesCategory = it
                        }

                        this.copyToRealm(expansesItem)
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to validate expenses")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error creating Expanses Item")
        }
    }

    override suspend fun updateExpenses(
        newExpenses: Expenses,
        expensesId: String
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateCategory =
                    validateExpensesCategory(newExpenses.expensesCategory?.expensesCategoryId ?: "")
                val validateExpensesPrice = validateExpensesPrice(newExpenses.expensesAmount)
                val validateExpensesDate = validateExpenseDate(newExpenses.expensesDate)

                val hasError = listOf(
                    validateCategory,
                    validateExpensesPrice,
                    validateExpensesDate
                ).any { !it.successful }

                if (!hasError) {
                    val expansesItem =
                        realm.query<ExpensesEntity>("expensesId == $0", expensesId).first().find()

                    if (expansesItem != null) {
                        realm.write {
                            val expansesCategory = this.query<ExpensesCategoryEntity>(
                                "expensesCategoryId == $0",
                                newExpenses.expensesCategory?.expensesCategoryId
                            ).first().find()

                            findLatest(expansesItem)?.apply {
                                this.expensesAmount = newExpenses.expensesAmount
                                this.expensesRemarks = newExpenses.expensesRemarks
                                this.expensesDate = newExpenses.expensesDate
                                this.createdAt = newExpenses.createdAt.ifEmpty {
                                    System.currentTimeMillis().toString()
                                }
                                this.updatedAt = System.currentTimeMillis().toString()
                                this.expensesCategory = expansesCategory
                            }
                        }

                        Resource.Success(true)
                    } else {
                        Resource.Error("Unable to find expenses")
                    }
                } else {
                    Resource.Error("Unable to validate expenses")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update expanses.")
        }
    }

    override suspend fun deleteExpense(expensesId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val expansesItem =
                    realm.query<ExpensesEntity>("expensesId == $0", expensesId).first().find()

                if (expansesItem != null) {
                    realm.write {
                        findLatest(expansesItem)?.let {
                            delete(it)
                        }
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to find expense item")
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete expanses.")
        }
    }

    override suspend fun deleteExpenses(expensesIds: List<String>): Resource<Boolean> {
        return try {
            expensesIds.forEach { expensesId ->
                withContext(ioDispatcher) {
                    val expansesItem =
                        realm.query<ExpensesEntity>("expensesId == $0", expensesId).first().find()

                    if (expansesItem != null) {
                        realm.write {
                            findLatest(expansesItem)?.let {
                                delete(it)
                            }
                        }
                    }
                }
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete expanses.")
        }
    }

    override suspend fun deletePastExpenses(deleteAll: Boolean): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val settings = settingsRepository.getSetting().data!!

                val expensesDate =
                    getCalculatedStartDate(days = "-${settings.expensesDataDeletionInterval}")

                realm.write {
                    val expenses = if (deleteAll) {
                        this.query<ExpensesEntity>().find()
                    } else {
                        this.query<ExpensesEntity>("createdAt < $0", expensesDate).find()
                    }

                    delete(expenses)
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete expanses")
        }
    }

    override suspend fun importExpenses(expenses: List<Expenses>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    expenses.forEach { expense ->
                        val data = this.query<ExpensesEntity>(
                            "expensesId == $0 OR expensesCategory.expensesCategoryId == $1 AND createdAt == $2",
                            expense.expensesId,
                            expense.expensesCategory?.expensesCategoryId,
                            expense.createdAt
                        ).first().find()

                        if (data == null && expense.expensesCategory != null) {
                            val newExpense = ExpensesEntity()
                            newExpense.expensesId =
                                expense.expensesId.ifEmpty { BsonObjectId().toHexString() }
                            newExpense.expensesAmount = expense.expensesAmount
                            newExpense.expensesRemarks = expense.expensesRemarks
                            newExpense.expensesDate = expense.expensesDate
                            newExpense.createdAt = expense.createdAt
                            newExpense.updatedAt = System.currentTimeMillis().toString()

                            val category = this.query<ExpensesCategoryEntity>(
                                "expensesCategoryId == $0 OR expensesCategoryName == $1",
                                expense.expensesCategory?.expensesCategoryId,
                                expense.expensesCategory?.expensesCategoryName
                            ).first().find()

                            if (category == null) {
                                val newCategory = this.copyToRealm(ExpensesCategoryEntity().apply {
                                    this.expensesCategoryId =
                                        expense.expensesCategory!!.expensesCategoryId
                                    this.expensesCategoryName =
                                        expense.expensesCategory!!.expensesCategoryName
                                    this.createdAt = expense.expensesCategory!!.createdAt
                                    this.updatedAt = System.currentTimeMillis().toString()
                                }, UpdatePolicy.ALL)

                                newExpense.expensesCategory = newCategory
                            } else {
                                newExpense.expensesCategory = category
                            }

                            this.copyToRealm(newExpense)
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to import expenses")
        }
    }

    override fun validateExpensesCategory(categoryId: String): ValidationResult {
        if (categoryId.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EXPENSE_NAME_EMPTY_ERROR,
            )
        }

        return ValidationResult(true)
    }

    override fun validateExpensesPrice(expansesPrice: String): ValidationResult {
        if (expansesPrice.isEmpty()) {
            return ValidationResult(
                false,
                EXPENSE_PRICE_EMPTY_ERROR
            )
        }

        if (expansesPrice.any { it.isLetter() }) {
            return ValidationResult(
                false,
                EXPENSES_PRICE_INVALID
            )
        }

        if (expansesPrice.toLong() < 10) {
            return ValidationResult(
                false,
                EXPENSE_PRICE_LESS_THAN_TEN_ERROR
            )
        }


        return ValidationResult(true)
    }

    override fun validateExpenseDate(expenseDate: String): ValidationResult {
        if (expenseDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EXPENSE_DATE_EMPTY_ERROR,
            )
        }

        return ValidationResult(true)
    }
}