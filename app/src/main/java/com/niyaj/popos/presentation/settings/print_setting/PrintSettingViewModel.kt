package com.niyaj.popos.presentation.settings.print_setting

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnections
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.niyaj.popos.domain.model.CartOrder
import com.niyaj.popos.domain.model.CartProduct
import com.niyaj.popos.domain.use_cases.order.OrderUseCases
import com.niyaj.popos.domain.use_cases.product.ProductUseCases
import com.niyaj.popos.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class PrintSettingViewModel @Inject constructor(
    private val productUseCases: ProductUseCases,
    private val orderUseCases: OrderUseCases,
): ViewModel() {

    private var deviceConnection: DeviceConnection? = null

    private var escposPrinter: EscPosPrinter

    private var bluetoothConnection: BluetoothConnection? = null

    private var bluetoothConnections: BluetoothConnections = BluetoothConnections()

    private var bluetoothPrintersConnections: BluetoothPrintersConnections = BluetoothPrintersConnections()

    private val _bluetoothDevices =  MutableStateFlow<List<BluetoothDeviceState>>(listOf())
    val bluetoothDevices = _bluetoothDevices.asStateFlow()

    private val _connectedDevice = MutableStateFlow<BluetoothConnection?>(null)

    init {
        escposPrinter = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), Constants.PRINTER_DPI, Constants.PRINTER_WIDTH_MM, Constants.PRINTER_NBR_LINE)
        _connectedDevice.tryEmit(BluetoothPrintersConnections.selectFirstPaired())

        getBluetoothPrinterDevices()
    }

    private fun getBluetoothDevices(){
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

    private fun getBluetoothPrinterDevices(){
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
        try{

            escposPrinter.disconnectPrinter()

            val device = bluetoothPrintersConnections.list?.find { it.device.address == address }

            if (device != null) {

                if(!device.isConnected){
                    device.connect()
                }

                _connectedDevice.tryEmit(device)

                escposPrinter = EscPosPrinter(device, 203, 58f, 32)

            }

        }catch (e: Exception){
            Timber.d(e.message ?: "Error connecting to Bluetooth")
        }
    }


    fun printOrder(cartOrderId: String) {
        viewModelScope.launch {
            val itemDetails = orderUseCases.getOrderDetails(cartOrderId).data
            var printItems = ""

            if(itemDetails != null) {
                Timber.d( "printing order details")

                printItems += printRestaurantDetails()
                printItems += printOrderDetails(itemDetails.cartOrder!!)
                printItems += printProductDetails(itemDetails.cartProducts)
                printItems += printTotalPrice(countTotalPrice(itemDetails.cartProducts).toString())
                printItems += printQrCode(cartOrderId)
                printItems += printFooterInfo()

                try {
                    escposPrinter.printFormattedText(printItems)
                }catch (e: Exception) {
                    Timber.d(e.message ?: "Error printing order details")
                }
            }
        }
    }

    private fun printRestaurantDetails(): String {
        return "[C]<b><font size='big'>POPOS HIGHLIGHT</font></b>\n" +
                "[C]-- Pure And Tasty --\n\n" +
                "[C]----------- POS BILL ----------\n\n"

    }

    private fun printOrderDetails(cartOrder: CartOrder): String {
        var order = ""

        order += "[L]Order Id - ${cartOrder.orderId}\n"

        order += "[L]Order Type - ${cartOrder.cartOrderType}\n"

        if(!cartOrder.customer?.customerPhone.isNullOrEmpty()){
            order += "[L]Customer Phone - ${cartOrder.customer?.customerPhone}\n"
        }

        if(!cartOrder.address?.addressName.isNullOrEmpty()){
            order += "[L]Customer Address - ${cartOrder.address?.addressName}\n"
        }

        return order
    }

    private fun printProductDetails(cartProduct: List<CartProduct>): String {
        var products = ""

        products += "[L]-------------------------------\n"

        products += "[L]Name[R]Qty[R]Price\n"

        products += "[L]-------------------------------\n"

        cartProduct.forEach {
            products += "[L]${it.product?.productName}[R]${it.quantity}[R] Rs. ${it.product?.productPrice}\n"
        }

        products += "[L]-------------------------------\n"

        return products
    }

    fun countTotalPrice(cartProducts: List<CartProduct>): Int {
        return cartProducts.sumOf {
            it.quantity?.times(it.product?.productPrice!!)!!
        }
    }

    private fun printTotalPrice(totalPrice: String): String {

        return "[L]-------------------------------\n" +
                "[L]Total[R] Rs. ${totalPrice}\n" +
                "[L]-------------------------------\n\n"
    }

    private fun printQrCode(orderId: String): String {
        return "[C]Pay by scanning this QR Code\n\n" +
                "[C]<qrcode size='20'>${orderId}</qrcode>\n\n\n"
    }

    private fun printFooterInfo(): String {
        return "[C]Thank you for ordering!\n" +
                "[C]For order and inquiry, Call.\n" +
                "[C]9500825077 / 9597185001\n"
    }
    
    private fun printAddOnItems(): String {
        return ""
    }

    private fun printCharges(): String {
        return ""
    }

    private fun printDiscount(): String {
        return ""
    }
}