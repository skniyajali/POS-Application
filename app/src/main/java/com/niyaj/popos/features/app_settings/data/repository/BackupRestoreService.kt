package com.niyaj.popos.features.app_settings.data.repository

import android.content.Context
import android.os.Environment
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
import com.niyaj.popos.features.reminder.domain.model.Reminder
import com.niyaj.popos.features.reports.domain.model.Reports
import dagger.hilt.android.qualifiers.ApplicationContext
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject


class BackupRestoreService @Inject constructor(
    config: RealmConfiguration,
    @ApplicationContext private val context : Context
) {

    private val realm = Realm.open(config)

    private val oldFile = config.name

    private val exportRealmFile =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    private val newFile = "backup.realm"
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
        Reminder::class,
    )
    private val newConfig = RealmConfiguration
        .Builder(schema)
        .name(newFile)
        .directory(exportRealmFile.path)
        .build()


    public fun backup() {
        try {
            val file = File(newConfig.path)

            if (file.exists()) {
                file.delete()
            }

            realm.writeCopyTo(newConfig)
        }catch (e: Exception) {
            Timber.e(e)
        }
    }

    public fun restore() {

        val restoreFilePath = newConfig.path

        copyBundledRealmFile(restoreFilePath, oldFile)
    }

    private fun copyBundledRealmFile(oldFilePath: String, outFileName: String) {
        try {
            val oldFile = File(context.filesDir, outFileName)
            val outputStream = FileOutputStream(oldFile)
            val inputStream = FileInputStream(File(oldFilePath))

            val buf = ByteArray(1024)
            var bytesRead : Int
            while (inputStream.read(buf).also { bytesRead = it } > 0) {
                outputStream.write(buf, 0, bytesRead)
            }
            outputStream.close()
        }catch (e: Exception) {
            Timber.e(e)
        }
    }
}