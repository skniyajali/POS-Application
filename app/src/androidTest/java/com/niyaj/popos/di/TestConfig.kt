@file:OptIn(ExperimentalCoroutinesApi::class)

package com.niyaj.popos.di

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.app_settings.domain.model.Settings
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.model.SelectedCartOrder
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo
import com.niyaj.popos.features.reports.domain.model.Reports
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import java.io.File
import java.nio.file.Files
import kotlin.io.path.absolutePathString
import kotlin.reflect.KClass

class TestConfig {
    @OptIn(ExperimentalCoroutinesApi::class)
    companion object {
        private val schema = setOf(
            Product::class,
            Category::class,
            Customer::class,
            Address::class,
            CartOrder::class,
            SelectedCartOrder::class,
            CartRealm::class,
            Charges::class,
            AddOnItem::class,
            ExpensesCategory::class,
            Expenses::class,
            Employee::class,
            EmployeeSalary::class,
            EmployeeAttendance::class,
            Reports::class,
            Settings::class,
            RestaurantInfo::class,
        )

        private val tmpDir = Files.createTempDirectory("android_tests").absolutePathString()
        private val realmConfig = RealmConfiguration.Builder(schema)
            .directory(tmpDir)
            .deleteRealmIfMigrationNeeded()
            .name("android_tests")
            .log(LogLevel.ALL)
            .build()

        operator fun invoke(): RealmConfiguration {
            return realmConfig
        }

        public fun configDir(): String {
            return tmpDir
        }

        public fun schema(): Set<KClass<out RealmObject>> {
            return schema
        }

        public fun config(): RealmConfiguration {
            return realmConfig
        }

        public fun clearDatabase(): Boolean {
            return File(tmpDir).deleteRecursively()
        }

        public fun testDispatcher(): TestDispatcher {
            return UnconfinedTestDispatcher(TestCoroutineScheduler())
        }

        public fun testDispatcher(scheduler: TestCoroutineScheduler): TestDispatcher {
            return UnconfinedTestDispatcher(scheduler)
        }

    }
}