package com.niyaj.data.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.data.mapper.toEntity
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
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class ExpensesCategoryRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher
) : ExpensesCategoryRepository, ExpCategoryValidationRepository {

    val realm = Realm.open(config)

    override suspend fun getAllExpensesCategory(searchText: String): Flow<List<ExpensesCategory>> {
        return withContext(ioDispatcher) {
            realm
                .query<ExpensesCategoryEntity>()
                .sort("expensesCategoryId", Sort.DESCENDING)
                .find()
                .asFlow()
                .mapLatest { categories ->
                    categories.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterExpensesCategory(searchText) },
                    )
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

    override suspend fun createOrUpdateCategory(
        newCategory: ExpensesCategory,
        categoryId: String
    ): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val validateName = validateName(newCategory.expensesCategoryName, categoryId)

                if (validateName.successful) {
                    withContext(ioDispatcher) {
                        val expansesCategory = realm.query<ExpensesCategoryEntity>(
                            "expensesCategoryId == $0",
                            categoryId
                        ).first().find()
                        if (expansesCategory != null) {
                            realm.write {
                                findLatest(expansesCategory)?.apply {
                                    this.expensesCategoryName = newCategory.expensesCategoryName
                                    this.updatedAt = System.currentTimeMillis().toString()
                                }
                            }

                            Resource.Success(true)
                        } else {
                            realm.write {
                                this.copyToRealm(newCategory.toEntity())
                            }
                            Resource.Success(true)
                        }
                    }
                } else {
                    Resource.Error("Unable to validate expenses category")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to update expanses category.")
            }
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

    private suspend fun findCategoryByNameAndId(
        categoryName: String,
        categoryId: String?
    ): Boolean {
        return withContext(ioDispatcher) {
            if (categoryId.isNullOrEmpty()) {
                realm.query<ExpensesCategoryEntity>("expensesCategoryName == $0", categoryName)
                    .first().find()
            } else {
                realm.query<ExpensesCategoryEntity>(
                    "expensesCategoryName == $0 AND expensesCategoryId != $1",
                    categoryName,
                    categoryId
                ).first().find()
            } != null
        }
    }

    override suspend fun validateName(categoryName: String, categoryId: String?): ValidationResult {
        if (categoryName.isEmpty()) return ValidationResult(false, "Category name is empty")

        if (categoryName.length < 3) return ValidationResult(false, "Invalid category name")

        if (categoryName.any { it.isDigit() }) return ValidationResult(
            false,
            "Category name must not contain any digit"
        )
        val result = withContext(ioDispatcher) {
            findCategoryByNameAndId(categoryName, categoryId)
        }
        if (result) {
            return ValidationResult(
                false,
                "Category name already exist"
            )
        }

        return ValidationResult(true)
    }
}