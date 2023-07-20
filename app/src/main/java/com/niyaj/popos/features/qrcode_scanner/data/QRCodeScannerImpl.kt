package com.niyaj.popos.features.qrcode_scanner.data

import com.google.android.gms.common.moduleinstall.ModuleInstallClient
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.niyaj.popos.features.qrcode_scanner.domain.repository.QRCodeScanner
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class QRCodeScannerImpl(
    private val scanner: GmsBarcodeScanner,
    private val playModule: ModuleInstallClient,
) : QRCodeScanner {

    init {
        playModule
            .areModulesAvailable(scanner)
            .addOnSuccessListener {
                if (!it.areModulesAvailable()) {
                    Timber.d("Downloading QR Code Module")
                    // Modules are not present on the device install...
//                    playModule.deferredInstall(scanner)
                    val newRequest = ModuleInstallRequest.newBuilder().addApi(scanner).build()
                    playModule.installModules(newRequest)
                }
            }.addOnFailureListener {
                Timber.d("Failed to install QRCodeScanner Module")
            }
    }

    override fun startScanning() : Flow<String?> {
        return callbackFlow {
            scanner.startScan()
                .addOnSuccessListener {
                    launch {
                        send(getDetails(it))
                    }
                }.addOnFailureListener {
                    launch {
                        send(null)
                    }
                }
            awaitClose {  }
        }
    }

    private fun getDetails(barcode : Barcode) : String? {
        return when (barcode.valueType) {
            Barcode.TYPE_WIFI -> {
                val ssid = barcode.wifi!!.ssid
                val password = barcode.wifi!!.password
                val type = barcode.wifi!!.encryptionType
                "ssid : $ssid, password : $password, type : $type"
            }

            Barcode.TYPE_URL -> {
                "${barcode.url?.url}"
            }

            Barcode.TYPE_PRODUCT -> {
                barcode.displayValue
            }

            Barcode.TYPE_EMAIL -> {
                "${barcode.email?.address}"
            }

            Barcode.TYPE_CONTACT_INFO -> {
                "${barcode.contactInfo?.name?.formattedName}"
            }

            Barcode.TYPE_PHONE -> {
                "${barcode.phone?.number}"
            }

            Barcode.TYPE_CALENDAR_EVENT -> {
                "${barcode.calendarEvent?.description}"
            }

            Barcode.TYPE_GEO -> {
                "${barcode.geoPoint?.lat} ${barcode.geoPoint?.lng}"
            }

            Barcode.TYPE_ISBN -> {
                barcode.displayValue
            }

            Barcode.TYPE_DRIVER_LICENSE -> {
                "${barcode.driverLicense?.firstName} ${barcode.driverLicense?.lastName }${barcode.driverLicense?.lastName}"
            }

            Barcode.TYPE_SMS -> {
                "${barcode.sms}"
            }

            Barcode.TYPE_TEXT -> {
                barcode.rawValue
            }

            Barcode.TYPE_UNKNOWN -> {
                "${barcode.rawValue}"
            }

            else -> {
                null
            }
        }
    }
}