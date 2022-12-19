package com.niyaj.popos.realm.data_deletion.data.repository

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.addon_item.domain.model.AddOnItem
import com.niyaj.popos.realm.address.domain.model.Address
import com.niyaj.popos.realm.app_settings.domain.model.Settings
import com.niyaj.popos.realm.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.realm.cart.CartRealm
import com.niyaj.popos.realm.cart_order.CartOrderRealm
import com.niyaj.popos.realm.cart_order.SelectedCartOrderRealm
import com.niyaj.popos.realm.category.domain.model.Category
import com.niyaj.popos.realm.charges.domain.model.Charges
import com.niyaj.popos.realm.customer.domain.model.Customer
import com.niyaj.popos.realm.data_deletion.domain.repository.DataDeletionRepository
import com.niyaj.popos.realm.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.realm.employee.domain.model.Employee
import com.niyaj.popos.realm.employee_attendance.AttendanceRealm
import com.niyaj.popos.realm.employee_salary.SalaryRealm
import com.niyaj.popos.realm.expenses.ExpensesRealm
import com.niyaj.popos.realm.expenses_category.ExpensesCategoryRealm
import com.niyaj.popos.realm.product.ProductRealm
import com.niyaj.popos.realm.reports.ReportsRealm
import com.niyaj.popos.util.getCalculatedStartDate
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DataDeletionRepositoryImpl(
    config: RealmConfiguration,
    private val settingsRepository: SettingsRepository
) : DataDeletionRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Data Deletion Session")
    }

    /**
     * ## Prerequisites
    -  Delete **CartOrder** data before today date.
    -  Delete **Cart** data before today start date.
    -  Generate **Report** Before Deleting Data
     * @return [Resource] of [Boolean] type
     */
    override suspend fun deleteData(): Resource<Boolean> {
        return try {
            val settings = settingsRepository.getSetting().data!!

            val expensesDate = getCalculatedStartDate(days = "-${settings.expensesDataDeletionInterval}")
            val cartDate = getCalculatedStartDate(days = "-${settings.cartDataDeletionInterval}")
            val cartOrderDate = getCalculatedStartDate(days = "-${settings.cartOrderDataDeletionInterval}")
            val reportDate = getCalculatedStartDate(days = "-${settings.reportDataDeletionInterval}")


            realm.write {
                val expenses = this.query<ExpensesRealm>("created_at < $0", reportDate).find()
                val carts = this.query<CartRealm>("created_at < $0", cartDate).find()
                val cartOrder = this.query<CartOrderRealm>("updated_at < $0", cartOrderDate).find()
                val reports = this.query<ReportsRealm>("createdAt < $0", expensesDate).find()

                delete(carts).also {
                    delete(cartOrder).also {
                        delete(reports).also {
                             delete(expenses)
                         }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete data", false)
        }
    }

    override suspend fun deleteAllRecords(): Resource<Boolean> {
        return try {
            CoroutineScope(Dispatchers.IO).launch {
                realm.write {
                    delete(Category::class)
                    delete(ProductRealm::class)
                    delete(Address::class)
                    delete(Customer::class)
                    delete(CartOrderRealm::class)
                    delete(CartRealm::class)
                    delete(AddOnItem::class)
                    delete(Charges::class)
                    delete(DeliveryPartner::class)
                    delete(Employee::class)
                    delete(ExpensesCategoryRealm::class)
                    delete(ExpensesRealm::class)
                    delete(SelectedCartOrderRealm::class)
                    delete(SalaryRealm::class)
                    delete(AttendanceRealm::class)
                    delete(ReportsRealm::class)
                    delete(Settings::class)
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete all Records", false)
        }
    }
}