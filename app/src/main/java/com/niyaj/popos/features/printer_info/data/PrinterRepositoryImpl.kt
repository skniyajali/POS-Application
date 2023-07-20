package com.niyaj.popos.features.printer_info.data

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.printer_info.domain.model.Printer
import com.niyaj.popos.features.printer_info.domain.model.PrinterRealm
import com.niyaj.popos.features.printer_info.domain.model.toPrinter
import com.niyaj.popos.features.printer_info.domain.model.toPrinterRealm
import com.niyaj.popos.features.printer_info.domain.repository.PrinterRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class PrinterRepositoryImpl(
    config : RealmConfiguration,
    private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO
) : PrinterRepository {

    val realm = Realm.open(config)

    override fun getPrinter(printerId : String) : Printer {
        return (realm.query<PrinterRealm>("printerId == $0", printerId).first().find()
            ?: PrinterRealm()).toPrinter()
    }

    override fun getPrinterInfo(printerId : String) : Flow<Printer> {
        return channelFlow {
            val data = realm.query<PrinterRealm>("printerId == $0", printerId).first().asFlow()

            data.collectLatest {
                when (it) {
                    is InitialObject -> {
                        send(it.obj.toPrinter())
                    }

                    is UpdatedObject -> {
                        send(it.obj.toPrinter())
                    }

                    else -> {
                        send(PrinterRealm().toPrinter())
                    }
                }
            }
        }
    }

    override suspend fun addOrUpdatePrinterInfo(printer : Printer) : Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val data =
                        this.query<PrinterRealm>("printerId == $0", printer.printerId).first()
                            .find()

                    if (data != null) {
                        data.printerDpi = printer.printerDpi
                        data.printerWidth = printer.printerWidth
                        data.printerNbrLines = printer.printerNbrLines
                        data.productNameLength = printer.productNameLength
                        data.productWiseReportLimit = printer.productWiseReportLimit
                        data.addressWiseReportLimit = printer.addressWiseReportLimit
                        data.customerWiseReportLimit = printer.customerWiseReportLimit
                        data.printQRCode = printer.printQRCode
                        data.printResLogo = printer.printResLogo
                        data.printWelcomeText = printer.printWelcomeText
                        data.createdAt = printer.createdAt
                        data.updatedAt = printer.updatedAt
                    } else {
                        this.copyToRealm(printer.toPrinterRealm())
                    }
                }
            }

            Resource.Success(true)
        } catch (e : Exception) {
            Resource.Error(e.message ?: "Something went wrong")
        }
    }
}