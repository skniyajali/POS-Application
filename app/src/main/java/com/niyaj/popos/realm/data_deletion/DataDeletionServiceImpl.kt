package com.niyaj.popos.realm.data_deletion

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.add_on_items.AddOnItemRealm
import com.niyaj.popos.realm.address.AddressRealm
import com.niyaj.popos.realm.app_settings.SettingsRealm
import com.niyaj.popos.realm.app_settings.SettingsService
import com.niyaj.popos.realm.cart.CartRealm
import com.niyaj.popos.realm.cart_order.CartOrderRealm
import com.niyaj.popos.realm.cart_order.SelectedCartOrderRealm
import com.niyaj.popos.realm.category.CategoryRealm
import com.niyaj.popos.realm.charges.ChargesRealm
import com.niyaj.popos.realm.customer.CustomerRealm
import com.niyaj.popos.realm.delivery_partner.PartnerRealm
import com.niyaj.popos.realm.employee.EmployeeRealm
import com.niyaj.popos.realm.employee_attendance.AttendanceRealm
import com.niyaj.popos.realm.employee_salary.SalaryRealm
import com.niyaj.popos.realm.expenses.ExpensesRealm
import com.niyaj.popos.realm.expenses_category.ExpensesCategoryRealm
import com.niyaj.popos.realm.product.ProductRealm
import com.niyaj.popos.realm.reports.ReportsRealm
import com.niyaj.popos.realmApp
import com.niyaj.popos.util.getCalculatedStartDate
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DataDeletionServiceImpl(
    config: SyncConfiguration,
    private val settingsService: SettingsService
) : DataDeletionService {

    private val user: User? = realmApp.currentUser

    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        if (user == null) {
            Timber.d("Data Deletion: user is null")
        } else {
            Timber.d("Data Deletion Session: $sessionState")

            CoroutineScope(Dispatchers.IO).launch {
                realm.subscriptions.waitForSynchronization()
                realm.syncSession.downloadAllServerChanges()
                realm.syncSession.uploadAllLocalChanges()
            }
        }
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
            val settings = settingsService.getSetting().data!!

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
//                    val category = this.query<CategoryRealm>().find()
//                    val products = this.query<ProductRealm>().find()
//                    val customers = this.query<CustomerRealm>().find()
//                    val cartOrder = this.query<CartOrderRealm>().find()
//                    val cart = this.query<CartRealm>().find()
//                    val addOnItems = this.query<AddOnItemRealm>().find()
//                    val charges = this.query<ChargesRealm>().find()
//                    val partner = this.query<PartnerRealm>().find()
//                    val employee = this.query<EmployeeRealm>().find()
//                    val expenses = this.query<ExpensesRealm>().find()
//                    val expensesCategory = this.query<ExpensesCategoryRealm>().find()
//                    val selectedCartOrder = this.query<SelectedCartOrderRealm>().find()
//                    val salary = this.query<SalaryRealm>().find()
//                    val attendance = this.query<AttendanceRealm>().find()
//                    val reports = this.query<ReportsRealm>().find()
//                    val settings = this.query<SettingsRealm>().find()

                    delete(CategoryRealm::class)
                    delete(ProductRealm::class)
                    delete(AddressRealm::class)
                    delete(CustomerRealm::class)
                    delete(CartOrderRealm::class)
                    delete(CartRealm::class)
                    delete(AddOnItemRealm::class)
                    delete(ChargesRealm::class)
                    delete(PartnerRealm::class)
                    delete(EmployeeRealm::class)
                    delete(ExpensesCategoryRealm::class)
                    delete(ExpensesRealm::class)
                    delete(SelectedCartOrderRealm::class)
                    delete(SalaryRealm::class)
                    delete(AttendanceRealm::class)
                    delete(ReportsRealm::class)
                    delete(SettingsRealm::class)
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete all Records", false)
        }
    }
}