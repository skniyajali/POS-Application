package com.niyaj.popos.features.data_deletion.data.repository

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.app_settings.domain.model.Settings
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.model.SelectedCartOrder
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.data_deletion.domain.repository.DataDeletionRepository
import com.niyaj.popos.features.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.reports.domain.model.Reports
import com.niyaj.popos.util.getCalculatedStartDate
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class DataDeletionRepositoryImpl(
    config: RealmConfiguration,
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
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

                val expensesDate = getCalculatedStartDate(days = "-${settings.expensesDataDeletionInterval}")
                val cartDate = getCalculatedStartDate(days = "-${settings.cartDataDeletionInterval}")
                val cartOrderDate = getCalculatedStartDate(days = "-${settings.cartOrderDataDeletionInterval}")
                val reportDate = getCalculatedStartDate(days = "-${settings.reportDataDeletionInterval}")


                realm.write {
                    val expenses = this.query<Expenses>("createdAt < $0", reportDate).find()
                    val carts = this.query<CartRealm>("createdAt < $0", cartDate).find()
                    val cartOrder = this.query<CartOrder>("updatedAt < $0", cartOrderDate).find()
                    val reports = this.query<Reports>("createdAt < $0", expensesDate).find()

                    delete(carts).also {
                        delete(cartOrder).also {
                            delete(reports).also {
                                delete(expenses)
                            }
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
                    delete(Product::class)
                    delete(Address::class)
                    delete(Customer::class)
                    delete(CartOrder::class)
                    delete(CartRealm::class)
                    delete(AddOnItem::class)
                    delete(Charges::class)
                    delete(DeliveryPartner::class)
                    delete(Employee::class)
                    delete(ExpensesCategory::class)
                    delete(Expenses::class)
                    delete(SelectedCartOrder::class)
                    delete(EmployeeSalary::class)
                    delete(EmployeeAttendance::class)
                    delete(Reports::class)
                    delete(Settings::class)
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete all Records", false)
        }
    }
}