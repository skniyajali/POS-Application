package com.niyaj.popos.realm.expenses

import com.niyaj.popos.domain.model.Expenses
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.app_settings.SettingsService
import com.niyaj.popos.realm.expenses_category.ExpensesCategoryRealm
import com.niyaj.popos.util.getCalculatedStartDate
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

class ExpensesRealmDaoImpl(
    config: RealmConfiguration,
    private val settingsService: SettingsService
) : ExpensesRealmDao {

    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        Timber.d("ExpensesRealmDao Session: $sessionState")
    }

    override suspend fun getAllExpanses(): Flow<Resource<List<ExpensesRealm>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val startDate = getCalculatedStartDate("-30")

                val expenses = realm.query<ExpensesRealm>("created_at >= $0", startDate)
                    .sort("_id", Sort.DESCENDING)
                    .find()

                val items = expenses.asFlow()

                items.collect { changes: ResultsChange<ExpensesRealm> ->
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

    override suspend fun getExpansesById(expansesId: String): Resource<ExpensesRealm?> {
        return try {

            val expansesItem = withContext(Dispatchers.IO) {
                realm.query<ExpensesRealm>("_id == $0", expansesId).first().find()
            }

            Resource.Success(expansesItem)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Expanses Item", null)
        }
    }

    override suspend fun createNewExpanses(newExpenses: Expenses): Resource<Boolean> {
        return try {
            val expansesItem = ExpensesRealm()
            expansesItem.expansesPrice = newExpenses.expansesPrice
            expansesItem.expansesRemarks = newExpenses.expansesRemarks

            realm.write {
                val expansesCategory = this.query<ExpensesCategoryRealm>(
                    "_id == $0",
                    newExpenses.expensesCategory.expensesCategoryId
                ).first().find()

                if (expansesCategory != null) {
                    expansesItem.expansesCategory = expansesCategory

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

    override suspend fun updateExpanses(
        newExpenses: Expenses,
        expansesId: String,
    ): Resource<Boolean> {
        return try {

            realm.write {
                val expansesCategory = this.query<ExpensesCategoryRealm>(
                    "_id == $0",
                    newExpenses.expensesCategory.expensesCategoryId
                ).find().first()

                val expansesItem = this.query<ExpensesRealm>("_id == $0", expansesId).first().find()
                expansesItem?.expansesPrice = newExpenses.expansesPrice
                expansesItem?.expansesRemarks = newExpenses.expansesRemarks
                expansesItem?.updated_at = System.currentTimeMillis().toString()

                findLatest(expansesCategory)?.also {
                    expansesItem?.expansesCategory = it
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update expanses.")
        }
    }

    override suspend fun deleteExpanses(expansesId: String): Resource<Boolean> {
        return try {
            realm.write {
                val expansesItem: ExpensesRealm =
                    this.query<ExpensesRealm>("_id == $0", expansesId).find().first()

                delete(expansesItem)
            }

            Resource.Success(true)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete expanses.")
        }
    }

    override suspend fun deletePastExpanses(deleteAll: Boolean): Resource<Boolean> {
        return try {
            val settings = settingsService.getSetting().data!!

            val expensesDate =
                getCalculatedStartDate(days = "-${settings.expensesDataDeletionInterval}")


            realm.write {
                val expenses = if (deleteAll) {
                    this.query<ExpensesRealm>().find()
                } else {
                    this.query<ExpensesRealm>("created_at < $0", expensesDate).find()
                }

                delete(expenses)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete expanses")
        }
    }
}