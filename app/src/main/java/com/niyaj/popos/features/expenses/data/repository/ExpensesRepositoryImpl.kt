package com.niyaj.popos.features.expenses.data.repository

import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository
import com.niyaj.popos.features.expenses.domain.repository.ExpensesValidationRepository
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.util.getCalculatedStartDate
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
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

class ExpensesRepositoryImpl(
    config: RealmConfiguration,
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ExpensesRepository, ExpensesValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("ExpensesRealmDao Session:")
    }

    override suspend fun getAllExpenses(startDate: String, endDate: String): Flow<Resource<List<Expenses>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val expenses = realm.query<Expenses>(
                        "createdAt >= $0 AND createdAt <= $1",
                        startDate,
                        endDate
                    ).sort("expensesId", Sort.DESCENDING).find()

                    val items = expenses.asFlow()

                    items.collect { changes: ResultsChange<Expenses> ->
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

                    send(Resource.Loading(false))
                } catch (e: Exception) {
                    send(Resource.Loading(false))
                    send(Resource.Error(e.message ?: "Unable to get expanses items", emptyList()))
                }
            }
        }
    }

    override suspend fun getExpensesById(expensesId: String): Resource<Expenses?> {
        return try {
            val expansesItem = withContext(ioDispatcher) {
                realm.query<Expenses>("expensesId == $0", expensesId).first().find()
            }

            Resource.Success(expansesItem)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Expanses Item", null)
        }
    }

    override suspend fun createNewExpenses(newExpenses: Expenses): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateExpensesCategory = validateExpensesCategory(newExpenses.expensesCategory?.expensesCategoryId ?: "")
                val validateExpensesPrice = validateExpensesPrice(newExpenses.expensesPrice)

                val hasError = listOf(validateExpensesCategory, validateExpensesPrice).any { !it.successful}

                if (!hasError) {
                    val expansesItem = Expenses()
                    expansesItem.expensesId = newExpenses.expensesId.ifEmpty { BsonObjectId().toHexString() }
                    expansesItem.expensesPrice = newExpenses.expensesPrice
                    expansesItem.expensesRemarks = newExpenses.expensesRemarks
                    expansesItem.createdAt = newExpenses.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    val expansesCategory = realm.query<ExpensesCategory>(
                        "expensesCategoryId == $0",
                        newExpenses.expensesCategory?.expensesCategoryId
                    ).first().find() ?: return@withContext Resource.Error("Unable to find expenses category", false)

                    realm.write {
                        findLatest(expansesCategory)?.let {
                            expansesItem.expensesCategory = it
                        }

                        this.copyToRealm(expansesItem)
                    }

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to validate expenses", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error creating Expanses Item", false)
        }
    }

    override suspend fun updateExpenses(newExpenses: Expenses, expensesId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateExpensesCategory = validateExpensesCategory(newExpenses.expensesCategory?.expensesCategoryId ?: "")
                val validateExpensesPrice = validateExpensesPrice(newExpenses.expensesPrice)

                val hasError = listOf(validateExpensesCategory, validateExpensesPrice).any { !it.successful}

                if (!hasError) {
                    val expansesItem = realm.query<Expenses>("expensesId == $0", expensesId).first().find()

                    if(expansesItem != null) {
                        realm.write {
                            val expansesCategory = this.query<ExpensesCategory>(
                                "expensesCategoryId == $0",
                                newExpenses.expensesCategory?.expensesCategoryId
                            ).first().find()

                            findLatest(expansesItem)?.apply {
                                this.expensesPrice = newExpenses.expensesPrice
                                this.expensesRemarks = newExpenses.expensesRemarks
                                this.createdAt = newExpenses.createdAt.ifEmpty { System.currentTimeMillis().toString() }
                                this.updatedAt = System.currentTimeMillis().toString()
                                this.expensesCategory = expansesCategory
                            }
                        }

                        Resource.Success(true)
                    }else {
                        Resource.Error("Unable to find expenses", false)
                    }
                }else {
                    Resource.Error("Unable to validate expenses", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update expanses.", false)
        }
    }

    override suspend fun deleteExpenses(expensesId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val expansesItem = realm.query<Expenses>("expensesId == $0", expensesId).first().find()

                if (expansesItem != null) {
                    realm.write {
                        findLatest(expansesItem)?.let {
                            delete(it)
                        }
                    }

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to find expense item", false)
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete expanses.", false)
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
                        this.query<Expenses>().find()
                    } else {
                        this.query<Expenses>("createdAt < $0", expensesDate).find()
                    }

                    delete(expenses)
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete expanses", false)
        }
    }

    override fun validateExpensesCategory(categoryId: String): ValidationResult {
        if (categoryId.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Category is required",
            )
        }

        return ValidationResult(true)
    }

    override fun validateExpensesPrice(expansesPrice: String): ValidationResult {
        if (expansesPrice.isEmpty()){
            return ValidationResult(
                false,
                "Expanses price must not be empty"
            )
        }

        if (expansesPrice.any { it.isLetter() }){
            return ValidationResult(
                false,
                "Expanses price must not contain a letter"
            )
        }

        if (expansesPrice.length > 6){
            return ValidationResult(
                false,
                "Invalid expanses price."
            )
        }


        return ValidationResult(true)
    }
}