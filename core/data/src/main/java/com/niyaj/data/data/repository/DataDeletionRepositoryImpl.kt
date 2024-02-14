package com.niyaj.data.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.getCalculatedStartDate
import com.niyaj.data.repository.DataDeletionRepository
import com.niyaj.data.repository.SettingsRepository
import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.database.model.AddressEntity
import com.niyaj.database.model.AttendanceEntity
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.CategoryEntity
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.CustomerEntity
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.ExpensesCategoryEntity
import com.niyaj.database.model.ExpensesEntity
import com.niyaj.database.model.PaymentEntity
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.ReportsEntity
import com.niyaj.database.model.SelectedCartOrderEntity
import com.niyaj.database.model.SettingsEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

class DataDeletionRepositoryImpl(
    config: RealmConfiguration,
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : DataDeletionRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Data Deletion Session")
    }

    /**
     * ### Prerequisites
    -  Delete **CartOrder** data before today date.
    -  Delete **Cart** data before today start date.
    -  Generate **Report** Before Deleting Data
     * @return [Resource] of [Boolean] type
     */
    override suspend fun deleteData(): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val settings = settingsRepository.getSetting().data!!

                val expensesDate =
                    getCalculatedStartDate(days = "-${settings.expensesDataDeletionInterval}")
                val cartDate =
                    getCalculatedStartDate(days = "-${settings.cartDataDeletionInterval}")
                val cartOrderDate =
                    getCalculatedStartDate(days = "-${settings.cartOrderDataDeletionInterval}")
                val reportDate =
                    getCalculatedStartDate(days = "-${settings.reportDataDeletionInterval}")

                withContext(ioDispatcher) {
                    realm.write {
                        val expenses = this.query<ExpensesEntity>("createdAt < $0", reportDate).find()
                        val carts = this.query<CartEntity>("createdAt < $0", cartDate).find()
                        val cartOrder =
                            this.query<CartOrderEntity>("updatedAt < $0", cartOrderDate).find()
                        val reports = this.query<ReportsEntity>("createdAt < $0", expensesDate).find()

                        delete(carts)
                        delete(cartOrder)
                        delete(reports)
                        delete(expenses)
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete data")
        }
    }

    override suspend fun deleteAllRecords(): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    delete(CategoryEntity::class)
                    delete(ProductEntity::class)
                    delete(AddressEntity::class)
                    delete(CustomerEntity::class)
                    delete(CartOrderEntity::class)
                    delete(CartEntity::class)
                    delete(AddOnItemEntity::class)
                    delete(ChargesEntity::class)
                    delete(EmployeeEntity::class)
                    delete(ExpensesCategoryEntity::class)
                    delete(ExpensesEntity::class)
                    delete(SelectedCartOrderEntity::class)
                    delete(PaymentEntity::class)
                    delete(AttendanceEntity::class)
                    delete(ReportsEntity::class)
                    delete(SettingsEntity::class)
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete all Records")
        }
    }
}