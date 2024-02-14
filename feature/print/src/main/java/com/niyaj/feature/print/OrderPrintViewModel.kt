package com.niyaj.feature.print

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.niyaj.common.utils.createDottedString
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.common.utils.toFormattedTime
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.data.repository.ChargesRepository
import com.niyaj.data.repository.OrderRepository
import com.niyaj.data.repository.RestaurantInfoRepository
import com.niyaj.feature.print.utils.BluetoothPrinter
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartOrder
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.DineOutOrder
import com.niyaj.model.OrderType
import com.niyaj.model.RestaurantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OrderPrintViewModel @Inject constructor(
    private val orderUseCases: OrderRepository,
    private val chargesRepository: ChargesRepository,
    private val repository: RestaurantInfoRepository,
    bluetoothPrinter: BluetoothPrinter,
    application: Application,
) : ViewModel() {

    private val escposPrinter = bluetoothPrinter.printer

    private var chargesList = mutableStateListOf<Charges>()

    private val info = bluetoothPrinter.info

    private var resInfo by mutableStateOf(RestaurantInfo())
    private val resLogo = resInfo.getRestaurantPrintLogo(application.applicationContext)

    private val _dineOutOrders = MutableStateFlow<List<DineOutOrder>>(emptyList())

    init {
        getAllCharges()
        getProfileInfo()
    }

    fun onPrintEvent(event: PrintEvent) {
        when (event) {
            is PrintEvent.PrintOrder -> {
                printOrder(event.cartOrder)
            }

            is PrintEvent.PrintOrders -> {
                printOrders(event.cartOrders)
            }

            is PrintEvent.PrintAllExpenses -> {

            }

            is PrintEvent.PrintDeliveryReport -> {
                getDineOutOrders(event.date)
                printDeliveryReport(event.date)
            }
        }
    }

    private fun printOrders(cartOrders: List<String>) {
        try {
            if (cartOrders.isNotEmpty()) {
                for (cartOrder in cartOrders) {
                    printOrder(cartOrder)
                }
            }
        } catch (e: Exception) {
            Timber.d(e.message ?: "Error connecting printer")
        }
    }

    private fun printOrder(cartOrderId: String) {
        viewModelScope.launch {
            var printItems = ""

            val itemDetails = orderUseCases.getOrderDetails(cartOrderId)

            if (itemDetails != null) {
                printItems += printRestaurantDetails()
                printItems += printOrderDetails(itemDetails.cartOrder!!)
                printItems += printProductDetails(itemDetails.orderedProducts)

                if (itemDetails.cartOrder!!.addOnItems.isNotEmpty()) {
                    printItems += printAddOnItems(itemDetails.cartOrder!!.addOnItems)
                }

                if (itemDetails.cartOrder!!.doesChargesIncluded && itemDetails.cartOrder!!.orderType != OrderType.DineIn) {
                    printItems += printCharges()
                }

                printItems += printSubTotalAndDiscount(itemDetails.orderPrice)
                printItems += printTotalPrice(itemDetails.orderPrice)
                printItems += printFooterInfo()
                printItems += printQrCode()

                try {
                    escposPrinter?.printFormattedTextAndCut(printItems, info.printerWidth)
                } catch (e: Exception) {
                    Timber.d(e.message ?: "Error printing order details")
                }
            }
        }
    }

    private fun printRestaurantDetails(): String {
        val imagePrint = PrinterTextParserImg.bitmapToHexadecimalString(escposPrinter, resLogo)

        var details = if (info.printResLogo) {
            "[C]<img>$imagePrint</img>\n\n"
        } else " \n"

        details += "[C]--------- ORDER BILL ---------\n\n"

        return details
    }

    private fun printOrderDetails(cartOrder: CartOrder): String {
        var order = ""

        order += "[L]ID - [R]${cartOrder.orderId}\n"

        order += "[L]Type - [R]${cartOrder.orderType}\n"

        order += "[L]Time - [R]${System.currentTimeMillis().toString().toFormattedTime}\n"

        if (!cartOrder.customer?.customerPhone.isNullOrEmpty()) {
            order += "[L]Phone - [R]${cartOrder.customer?.customerPhone}\n"
        }

        if (!cartOrder.address?.addressName.isNullOrEmpty()) {
            order += "[L]Address - [R]${cartOrder.address?.addressName}\n"
        }

        return order
    }

    private fun printProductDetails(orderedProduct: List<CartProductItem>): String {
        var products = ""

        products += "[L]-------------------------------\n"

        products += "[L]Name[R]Qty[R]Price\n"

        products += "[L]-------------------------------\n"

        orderedProduct.forEach {
            val productName = createDottedString(it.productName, info.productNameLength)

            products += "[L]${productName}[R]${it.productQuantity}[R]${it.productPrice}\n"
        }

        return products
    }

    private fun printTotalPrice(orderPrice: Pair<Int, Int>): String {
        return "[L]-------------------------------\n" +
                "[L]Total[R] Rs. ${orderPrice.first.minus(orderPrice.second)}\n" +
                "[L]-------------------------------\n\n"
    }

    private fun printQrCode(): String {
        return if (info.printQRCode) {
            "[C]Pay by scanning this QR code\n\n" +
                    "[L]\n" +
                    "[C]<qrcode size ='40'>${resInfo.paymentQrCode}</qrcode>\n\n\n" +
                    "[C]Good Food, Good Mood\n\n" +
                    "[L]-------------------------------\n"
        } else ""
    }

    private fun printFooterInfo(): String {
        return if (info.printWelcomeText) {
            "[C]Thank you for ordering!\n" +
                    "[C]For order and inquiry, Call.\n" +
                    "[C]${resInfo.primaryPhone} / ${resInfo.secondaryPhone}\n\n"
        } else ""
    }

    private fun printAddOnItems(addOnItemList: List<AddOnItem>): String {
        var addOnItems = ""

        if (addOnItemList.isNotEmpty()) {
            addOnItems += "[L]-------------------------------\n"
            for (addOnItem in addOnItemList) {
                addOnItems += "[L]${addOnItem.itemName}[R]${addOnItem.itemPrice}\n"
            }

        }

        return addOnItems
    }

    private fun printCharges(): String {
        var charges = ""

        if (chargesList.isNotEmpty()) {
            charges += "[L]-------------------------------\n"
            for (charge in chargesList) {
                charges += "[L]${charge.chargesName}[R]${charge.chargesPrice.toString().toRupee}\n"
            }
        }

        return charges
    }

    private fun printSubTotalAndDiscount(orderPrice: Pair<Int, Int>): String {
        return "[L]-------------------------------\n" +
                "[L]Sub Total[R]${orderPrice.first}\n" +
                "[L]Discount[R]${orderPrice.second}\n"
    }

    private fun getAllCharges() {
        viewModelScope.launch {
            chargesRepository.getAllCharges("").collect { result ->
                chargesList = result.toMutableStateList()
            }
        }
    }

    private fun getProfileInfo() {
        viewModelScope.launch {
            repository.getRestaurantInfo().collectLatest { result ->
                resInfo = result
            }
        }
    }

    private fun printDeliveryReport(date: String) {
        try {
            var printItems = ""

            printItems += getPrintableHeader(date)
            printItems += getPrintableOrders()

            escposPrinter?.printFormattedText(printItems, 50)
        } catch (e: Exception) {
            Timber.e("Print Exception", e.message ?: "Unable to print")
        }
    }

    private fun getPrintableHeader(date: String): String {
        var header = "[C]<b><font size='big'>DELIVERY</font></b>\n\n"

        header += if (date.isEmpty()) {
            "[C]--------- ${System.currentTimeMillis().toString().toFormattedDate} --------\n"
        } else {
            "[C]----------${date.toFormattedDate}---------\n"
        }

        header += "[L]\n"

        return header
    }

    private fun getPrintableOrders(): String {
        var order = ""

        val dineOutOrders = _dineOutOrders.value

        if (dineOutOrders.isNotEmpty()) {
            order += "[L]ID[C]Address[R]Time[R]Price\n"
            order += "[L]-------------------------------\n"

            dineOutOrders.forEach { cart ->
                order += "[L]${cart.orderId.takeLast(3)}[C]${cart.customerAddress}[R]${cart.updatedAt.toTime}[R]${cart.totalAmount}\n"
                order += "[L]-------------------------------\n"
            }
        } else {
            order += "[C]You have not place any order.\n"
        }

        order += "[L]\n"

        return order
    }

    private fun getDineOutOrders(date: String) {
        viewModelScope.launch {
            orderUseCases.getDineOutOrders("", date).collectLatest {
                _dineOutOrders.value = it
            }
        }
    }
}
