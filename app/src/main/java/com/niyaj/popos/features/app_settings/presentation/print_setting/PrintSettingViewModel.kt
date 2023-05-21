package com.niyaj.popos.features.app_settings.presentation.print_setting

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnections
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.niyaj.popos.features.order.domain.repository.OrderRepository
import com.niyaj.popos.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class PrintSettingViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
) : ViewModel() {

    private var _deviceConnection: DeviceConnection? = null

    private var escposPrinter: EscPosPrinter

    private var bluetoothConnection: BluetoothConnection? = null

    private var bluetoothConnections: BluetoothConnections = BluetoothConnections()

    private var bluetoothPrintersConnections: BluetoothPrintersConnections =
        BluetoothPrintersConnections()

    private val _bluetoothDevices = MutableStateFlow<List<BluetoothDeviceState>>(listOf())
    val bluetoothDevices = _bluetoothDevices.asStateFlow()

    private val _connectedDevice = MutableStateFlow<BluetoothConnection?>(null)

    init {
        escposPrinter = EscPosPrinter(
            BluetoothPrintersConnections.selectFirstPaired(),
            Constants.PRINTER_DPI,
            Constants.PRINTER_WIDTH_MM,
            Constants.PRINTER_NBR_LINE
        )
        _connectedDevice.tryEmit(BluetoothPrintersConnections.selectFirstPaired())

        getBluetoothPrinterDevices()
    }

    private fun getBluetoothDevices() {
        val connections = bluetoothConnections.list?.map {
            BluetoothDeviceState(
                name = it.device.name,
                address = it.device.address,
                bondState = it.device.bondState,
                type = it.device.type,
                connected = it.isConnected
            )
        }

        if (connections != null) {
            _bluetoothDevices.tryEmit(connections)
        }
    }

    private fun getBluetoothPrinterDevices() {
        val connections = bluetoothPrintersConnections.list?.map {
            BluetoothDeviceState(
                name = it.device.name,
                address = it.device.address,
                bondState = it.device.bondState,
                type = it.device.type,
                connected = it.isConnected,
            )
        }

        if (connections != null) {
            _bluetoothDevices.tryEmit(connections)
        }
    }

    fun connectBluetoothPrinter(address: String) {
        try {

            escposPrinter.disconnectPrinter()

            val device = bluetoothPrintersConnections.list?.find { it.device.address == address }

            if (device != null) {

                if (!device.isConnected) {
                    device.connect()
                }

                _connectedDevice.tryEmit(device)

                escposPrinter = EscPosPrinter(device, 203, 58f, 32)

            }

        } catch (e: Exception) {
            Timber.d(e.message ?: "Error connecting to Bluetooth")
        }
    }
}