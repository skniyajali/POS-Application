package com.niyaj.database.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import com.niyaj.common.utils.Constants.BACKUP_REALM_NAME
import com.niyaj.common.utils.Resource
import com.niyaj.database.model.AccountEntity
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
import com.niyaj.database.model.PrinterEntity
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.ReminderEntity
import com.niyaj.database.model.ReportsEntity
import com.niyaj.database.model.RestaurantInfoEntity
import com.niyaj.database.model.SelectedCartOrderEntity
import com.niyaj.database.model.SettingsEntity
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
    config: RealmConfiguration,
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
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
        ProductEntity::class,
        CategoryEntity::class,
        CustomerEntity::class,
        AddressEntity::class,
        CartOrderEntity::class,
        SelectedCartOrderEntity::class,
        CartEntity::class,
        ChargesEntity::class,
        AddOnItemEntity::class,
        ExpensesCategoryEntity::class,
        ExpensesEntity::class,
        EmployeeEntity::class,
        PaymentEntity::class,
        AttendanceEntity::class,
        ReportsEntity::class,
        SettingsEntity::class,
        RestaurantInfoEntity::class,
        ReminderEntity::class,
        AccountEntity::class,
        PrinterEntity::class,
    )

    suspend fun backupDatabase(): Resource<Boolean> {
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
        } catch (e: Exception) {
            Timber.e(e)

            Resource.Error(e.message ?: "Something went wrong")
        }
    }

    suspend fun restoreDatabase(): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                backupFileDirectory?.let {
                    copyBundledRealmFile(backupFileDirectory.path, originalFileName)
                }
            }

            Resource.Success(true)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong")
        }
    }

    private fun copyBundledRealmFile(backupFilePath: String, originalFileName: String) {
        try {
            val originalFile = File(context.filesDir, originalFileName)

            val outputStream = FileOutputStream(originalFile)
            val inputStream = FileInputStream(File(backupFilePath, backupFileName))

            val buf = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buf).also { bytesRead = it } > 0) {
//                outputStream.flush()
                outputStream.write(buf, 0, bytesRead)
            }

            outputStream.close()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}