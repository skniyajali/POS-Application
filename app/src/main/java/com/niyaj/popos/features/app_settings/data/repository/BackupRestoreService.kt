package com.niyaj.popos.features.app_settings.data.repository

import android.content.Context
import android.os.Build
import android.os.Environment
import com.niyaj.popos.common.utils.Constants.BACKUP_REALM_NAME
import com.niyaj.popos.features.account.domain.model.Account
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.app_settings.domain.model.Settings
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.model.SelectedCartOrder
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.features.printer_info.domain.model.PrinterRealm
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo
import com.niyaj.popos.features.reminder.domain.model.Reminder
import com.niyaj.popos.features.reports.domain.model.Reports
import dagger.hilt.android.qualifiers.ApplicationContext
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject


class BackupRestoreService @Inject constructor(
    config : RealmConfiguration,
    @ApplicationContext private val context : Context,
    private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO,
) {

    private val realm = Realm.open(config)

    private val originalFileName = config.name

    private val backupFileDirectory =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }

    private val backupFileName = BACKUP_REALM_NAME

    private val backupSchema = setOf(
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
        Account::class,
        PrinterRealm::class,
    )

    suspend fun backupDatabase() : Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                backupFileDirectory?.let {
                    val backupConfig = RealmConfiguration
                        .Builder(backupSchema)
                        .name(backupFileName)
                        .schemaVersion(realm.schemaVersion())
                        .directory(backupFileDirectory.path)
                        .build()

                    val file = File(backupFileDirectory.path, backupFileName)

                    if (file.exists()) {
                        file.delete()
                    } else {
                        Timber.d("File not found")
                    }

                    realm.writeCopyTo(backupConfig)

                    Resource.Success(true)
                } ?: Resource.Error("Unable to access backup directory")
            }
        } catch (e : Exception) {
            Timber.e(e)

            Resource.Error(e.message ?: "Something went wrong", false)
        }
    }

    suspend fun restoreDatabase() : Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                backupFileDirectory?.let {
                    copyBundledRealmFile(backupFileDirectory.path, originalFileName)
                }
            }

            Resource.Success(true)

        } catch (e : Exception) {
            Resource.Error(e.message ?: "Something went wrong", false)
        }
    }

    private fun copyBundledRealmFile(backupFilePath : String, originalFileName : String) {
        try {
            val originalFile = File(context.filesDir, originalFileName)

            val outputStream = FileOutputStream(originalFile)
            val inputStream = FileInputStream(File(backupFilePath, backupFileName))

            val buf = ByteArray(1024)
            var bytesRead : Int

            while (inputStream.read(buf).also { bytesRead = it } > 0) {
//                outputStream.flush()
                outputStream.write(buf, 0, bytesRead)
            }

            outputStream.close()
        } catch (e : Exception) {
            Timber.e(e)
        }
    }
}