package com.niyaj.feature.print.utils

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.model.BluetoothDeviceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import timber.log.Timber
import javax.inject.Inject

/**
 * BluetoothPrinter is a class that provides functionality for connecting to and interacting with a Bluetooth printer.
 * It allows retrieving available printers, connecting to specific printers, and printing data.
 *
 * @param printerRepository The repository for retrieving printer information.
 */
class BluetoothPrinter @Inject constructor(
    private val printerRepository : PrinterRepository,
) {

    /**
     * Retrieves printer information from the printer repository.
     */
    val info = printerRepository.getPrinter()

    /**
     * The mutable state property holding an EscPosPrinter object.
     */
    private val _printer = mutableStateOf<EscPosPrinter?>(null)

    /**
     * Provides read-only access to the printer property.
     */
    val printer : EscPosPrinter?
        get() = _printer.value

    /**
     * The mutable state property holding a BluetoothConnection object.
     */
    private val _bluetoothConnection = mutableStateOf<BluetoothConnection?>(null)

    /**
     * Provides read-only access to the bluetoothConnection property.
     */
    private val bluetoothConnection : BluetoothConnection?
        get() = _bluetoothConnection.value

    /**
     * The BluetoothPrintersConnections instance for managing Bluetooth printer connections.
     */
    private val connections = BluetoothPrintersConnections()

    /**
     * Initializes the BluetoothPrinter class and connect first paired bluetooth printer.
     */
    init {
        connectBluetoothPrinter()
    }

    /**
     * Retrieves a flow of BluetoothDeviceState objects representing the available Bluetooth printers.
     *
     * @return A flow emitting a list of BluetoothDeviceState objects.
     */
    @SuppressLint("MissingPermission")
    fun getBluetoothPrinters() : Flow<List<BluetoothDeviceState>> {
        return channelFlow {
            try {
                val list = connections.list?.map {
                    BluetoothDeviceState(
                        name = it.device.name,
                        address = it.device.address,
                        bondState = it.device.bondState,
                        type = it.device.type,
                        connected = it.isConnected,
                    )
                }

                list?.let {
                    send(it)
                }
            }catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    /**
     * Connects to a Bluetooth printer with the specified address.
     *
     * @param address The address of the Bluetooth printer.
     */
    fun connectBluetoothPrinter(address : String) {
        try {
            val device = connections.list?.find { it.device.address == address }
            device?.connect()

            _bluetoothConnection.value = device
            _printer.value = EscPosPrinter(
                device,
                info.printerDpi,
                info.printerWidth,
                info.printerNbrLines
            )
        } catch (e : Exception) {
            Timber.d(e.message ?: "Error connecting to Bluetooth")
        }
    }

    /**
     * Connects to the first paired Bluetooth printer.
     */
    private fun connectBluetoothPrinter() {
        try {
            val data = BluetoothPrintersConnections.selectFirstPaired()
            data?.connect()

            _bluetoothConnection.value = data
            _printer.value = EscPosPrinter(
                data,
                info.printerDpi,
                info.printerWidth,
                info.printerNbrLines
            )
        } catch (e : Exception) {
            Timber.d(e.message ?: "Error connecting to Bluetooth")
        }
    }

    /**
     * Prints test data using the printer object.
     */
    fun printTestData() {
        printer?.printFormattedText("[C]<b><font size='big'>Testing</font></b> \n")
    }
}
