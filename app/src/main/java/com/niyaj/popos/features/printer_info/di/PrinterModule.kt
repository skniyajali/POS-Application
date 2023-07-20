package com.niyaj.popos.features.printer_info.di

import com.niyaj.popos.features.printer_info.data.PrinterRepositoryImpl
import com.niyaj.popos.features.printer_info.data.PrinterValidationRepositoryImpl
import com.niyaj.popos.features.printer_info.domain.repository.PrinterRepository
import com.niyaj.popos.features.printer_info.domain.repository.PrinterValidationRepository
import com.niyaj.popos.features.printer_info.domain.utils.BluetoothPrinter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrinterModule {

    @Provides
    fun providePrinterRepository(config : RealmConfiguration) : PrinterRepository {
        return PrinterRepositoryImpl(config)
    }


    @Provides
    fun providePrinterValidationRepository() : PrinterValidationRepository {
        return PrinterValidationRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideBluetoothPrinter(
        printerRepository : PrinterRepository
    ): BluetoothPrinter {
        return BluetoothPrinter(printerRepository)
    }
}