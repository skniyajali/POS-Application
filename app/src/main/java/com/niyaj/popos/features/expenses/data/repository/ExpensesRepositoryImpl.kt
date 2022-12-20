package com.niyaj.popos.features.expenses.data.repository

import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.util.getCalculatedStartDate
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class ExpensesRepositoryImpl(
    config: RealmConfiguration,
    private val settingsRepository: SettingsRepository
) : ExpensesRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("ExpensesRealmDao Session:")
    }

    override suspend fun getAllExpenses(): Flow<Resource<List<Expenses>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val startDate = getCalculatedStartDate("-30")

                val expenses = realm.query<Expenses>("createdAt >= $0", startDate)
                    .sort("expensesId", Sort.DESCENDING)
                    .find()

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
                send(Resource.Error(e.message ?: "Unable to get expanses items", null))
            }
        }
    }

    override suspend fun getExpensesById(expensesId: String): Resource<Expenses?> {
        return try {

            val expansesItem = withContext(Dispatchers.IO) {
                realm.query<Expenses>("expensesId == $0", expensesId).first().find()
            }

            Resource.Success(expansesItem)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Expanses Item", null)
        }
    }

    override suspend fun createNewExpenses(newExpenses: Expenses): Resource<Boolean> {
        return try {
            val expansesItem = Expenses()
            expansesItem.expensesId = BsonObjectId().toHexString()
            expansesItem.expensesPrice = newExpenses.expensesPrice
            expansesItem.expensesRemarks = newExpenses.expensesRemarks
            expansesItem.createdAt = System.currentTimeMillis().toString()

            realm.write {
                val expansesCategory = this.query<ExpensesCategory>(
                    "expensesCategoryId == $0",
                    newExpenses.expensesCategory?.expensesCategoryId
                ).first().find()

                if (expansesCategory != null) {
                    expansesItem.expensesCategory = expansesCategory

                    this.copyToRealm(expansesItem)

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to find expense category", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error creating Expanses Item", false)
        }
    }

    override suspend fun updateExpenses(
        newExpenses: Expenses,
        expensesId: String,
    ): Resource<Boolean> {
        return try {

            realm.write {
                val expansesCategory = this.query<ExpensesCategory>(
                    "expensesCategoryId == $0",
                    newExpenses.expensesCategory?.expensesCategoryId
                ).find().first()

                val expansesItem = this.query<Expenses>("expensesId == $0", expensesId).first().find()
                expansesItem?.expensesPrice = newExpenses.expensesPrice
                expansesItem?.expensesRemarks = newExpenses.expensesRemarks
                expansesItem?.updatedAt = System.currentTimeMillis().toString()

                findLatest(expansesCategory)?.also {
                    expansesItem?.expensesCategory = it
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update expanses.")
        }
    }

    override suspend fun deleteExpenses(expensesId: String): Resource<Boolean> {
        return try {
            realm.write {
                val expansesItem: Expenses =
                    this.query<Expenses>("expensesId == $0", expensesId).find().first()

                delete(expansesItem)
            }

            Resource.Success(true)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete expanses.")
        }
    }

    override suspend fun deletePastExpenses(deleteAll: Boolean): Resource<Boolean> {
        return try {
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

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete expanses")
        }
    }
}