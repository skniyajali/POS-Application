package com.niyaj.popos.features.qrcode_scanner.di

import android.app.Application
import android.content.Context
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallClient
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.niyaj.popos.features.qrcode_scanner.data.QRCodeScannerImpl
import com.niyaj.popos.features.qrcode_scanner.domain.repository.QRCodeScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ScannerModule {

    @Provides
    fun provideContext(app: Application):Context{
        return app.applicationContext
    }

    @Provides
    fun provideBarCodeOptions() : GmsBarcodeScannerOptions {
        return GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    }

    @Provides
    fun provideGooglePlayModule(context: Context) : ModuleInstallClient {
        return ModuleInstall.getClient(context)
    }

    @Provides
    fun provideBarCodeScanner(context: Context, options: GmsBarcodeScannerOptions): GmsBarcodeScanner {
        return GmsBarcodeScanning.getClient(context, options)
    }

    @Provides
    fun provideBarCodeScannerRepository(scanner : GmsBarcodeScanner, playModule : ModuleInstallClient): QRCodeScanner {
        return QRCodeScannerImpl(scanner, playModule)
    }
}