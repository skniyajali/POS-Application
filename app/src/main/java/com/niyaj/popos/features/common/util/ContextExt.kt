package com.niyaj.popos.features.common.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

@RequiresApi(Build.VERSION_CODES.S)
fun Context.hasBluetoothPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.BLUETOOTH
    ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED &&

        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_ADMIN
        ) == PackageManager.PERMISSION_GRANTED &&

        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

}

@RequiresApi(Build.VERSION_CODES.S)
fun Context.hasStoragePermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&

            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
}