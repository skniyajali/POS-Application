package com.niyaj.popos.features.common.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.niyaj.popos.util.Constants.FILE_URI
import com.niyaj.popos.util.Constants.JSON_FILE_EXTENSION
import com.niyaj.popos.util.Constants.JSON_FILE_TYPE
import com.niyaj.popos.util.Constants.SAVEABLE_FILE_NAME
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException

object ImportExport {

    @RequiresApi(Build.VERSION_CODES.Q)
    internal fun openFile(
        pickerInitialUri: Uri = FILE_URI,
    ): Intent {
        val intent = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            pickerInitialUri
        ).apply {
            type = JSON_FILE_TYPE
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        return intent
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    internal fun createFile(fileName: String = SAVEABLE_FILE_NAME): Intent {
        val intent = Intent(
            Intent.ACTION_CREATE_DOCUMENT,
            FILE_URI
        ).apply {
            type = JSON_FILE_TYPE
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, fileName.plus(JSON_FILE_EXTENSION))
        }

        return intent
    }

    @OptIn(ExperimentalStdlibApi::class)
    internal inline fun <reified T> writeData(context: Context, uri: Uri, data: List<T>): Boolean {
        try {

            val moshi = Moshi.Builder().build().adapter<List<T>>()
            val list = moshi.toJson(data)

            context.applicationContext.contentResolver.openOutputStream(uri, "rwt")?.use {
                it.flush()
                it.bufferedWriter().use { writer ->
                    writer.write(list)
                }
            }

            return true

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    internal inline fun <reified T> readData(context: Context, uri: Uri): List<T> {
        try {
            val container = mutableListOf<T>()

            val moshi = Moshi.Builder().build().adapter<List<T>>()

            context.applicationContext.contentResolver.openInputStream(uri)?.use {
                it.bufferedReader().use { reader ->
                    moshi.fromJson(reader.readLine())?.let { it1 -> container.addAll(it1) }
                }

                it.close()
            }

            return container.toList()
        } catch (e: FileNotFoundException) {
            Timber.e(e)
            return emptyList()
        } catch (e: IOException) {
            Timber.e(e)
            return emptyList()
        } catch (e: Exception) {
            Timber.e(e)
            return emptyList()
        }
    }
}
