package com.niyaj.popos.features.order.presentation.print_order

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.cart.domain.model.CartProduct
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.domain.use_cases.ChargesUseCases
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.order.domain.use_cases.OrderUseCases
import com.niyaj.popos.util.Constants
import com.niyaj.popos.util.toFormattedDateAndTime
import com.niyaj.popos.util.toRupee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class PrintViewModel @Inject constructor(
    private val orderUseCases: OrderUseCases,
    private val chargesUseCases: ChargesUseCases,
) : ViewModel() {

    private lateinit var escposPrinter: EscPosPrinter
    private var chargesList = mutableStateListOf<Charges>()

    init {
        getAllCharges()
    }


    fun onPrintEvent(event: PrintEvent){
        when (event){
            is PrintEvent.PrintOrder -> {
                connectPrinter()
                printOrder(event.cartOrder)
            }

            is PrintEvent.PrintOrders -> {
                connectPrinter()
                printOrders(event.cartOrders)

            }
            is PrintEvent.PrintAllExpenses -> {

            }

        }
    }

    private fun connectPrinter(): Boolean {
        try {
            escposPrinter = EscPosPrinter(
                BluetoothPrintersConnections.selectFirstPaired(),
                Constants.PRINTER_DPI,
                Constants.PRINTER_WIDTH_MM,
                Constants.PRINTER_NBR_LINE
            )

            return true
        } catch (e: Exception) {
            throw e
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

            val itemDetails = orderUseCases.getOrderDetails(cartOrderId).data

            if (itemDetails != null) {
                printItems += printRestaurantDetails()
                printItems += printOrderDetails(itemDetails.cartOrder!!)
                printItems += printProductDetails(itemDetails.cartProducts)

                if (itemDetails.cartOrder.addOnItems.isNotEmpty()){
                    printItems += printAddOnItems(itemDetails.cartOrder.addOnItems)
                }

                if(itemDetails.cartOrder.doesChargesIncluded && itemDetails.cartOrder.orderType != CartOrderType.DineIn.orderType){
                    printItems += printCharges()
                }

                printItems += printSubTotalAndDiscount(itemDetails.orderPrice)
                printItems += printTotalPrice(itemDetails.orderPrice)
                printItems += printFooterInfo()
                printItems += printQrCode()

                try {
                    escposPrinter.printFormattedTextAndCut(printItems, 50)
                } catch (e: Exception) {
                    Timber.d(e.message ?: "Error printing order details")
                }

//                val printerCommands =
//                    EscPosPrinterCommands(BluetoothPrintersConnections.selectFirstPaired())
//                try {
//                    printerCommands.connect()
//                    printerCommands.reset()
//                    printerCommands.feedPaper(50)
//                    printerCommands.cutPaper()
//                } catch (e: EscPosConnectionException) {
//                    e.printStackTrace()
//                }
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

        order += "[L]Order ID - ${cartOrder.orderId}\n"

        order += "[L]Order Type - ${cartOrder.orderType}\n"

        if (!cartOrder.customer?.customerPhone.isNullOrEmpty()) {
            order += "[L]Phone - ${cartOrder.customer?.customerPhone}\n"
        }

        if (!cartOrder.address?.addressName.isNullOrEmpty()) {
            order += "[L]Address - ${cartOrder.address?.addressName}\n"
        }

        order += "[L]Date - ${System.currentTimeMillis().toString().toFormattedDateAndTime}\n"

        return order
    }

    private fun printProductDetails(cartProduct: List<CartProduct>): String {
        var products = ""

        products += "[L]-------------------------------\n"

        products += "[L]Name[R]Qty[R]Price\n"

        products += "[L]-------------------------------\n"

        cartProduct.forEach {
            products += "[L]${it.product?.productName}[R]${it.quantity}[R]${it.product?.productPrice}\n"
        }

        return products
    }

    private fun printTotalPrice(orderPrice: Pair<Int, Int>): String {
        return "[L]-------------------------------\n" +
                "[L]Total[R] Rs. ${orderPrice.first.minus(orderPrice.second)}\n" +
                "[L]-------------------------------\n\n"
    }

    private fun printQrCode(): String {
        return "[C]Pay by scanning this QR code\n\n"+
                "[L]\n" +
                "[C]<qrcode size ='40'>${Constants.PRINTER_QR_DATA}</qrcode>\n\n\n\n" +
                "[L]-------------------------------\n\n\n"

    }

    private fun printFooterInfo(): String {
        return "[C]Thank you for ordering!\n" +
                "[C]For order and inquiry, Call.\n" +
                "[C]9500825077 / 9597185001\n\n"
    }

    private fun printAddOnItems(addOnItemList: List<AddOnItem>): String {
        var addOnItems = ""

        if(addOnItemList.isNotEmpty()){
            addOnItems += "[L]-------------------------------\n"
            for (addOnItem in addOnItemList){
                addOnItems += "[L]${addOnItem.itemName}[R]${addOnItem.itemPrice}\n"
            }

        }
        return addOnItems
    }

    private fun printCharges(): String {
        var charges = ""

        if (chargesList.isNotEmpty()){
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

    private fun getAllCharges(){
        viewModelScope.launch {
            chargesUseCases.getAllCharges().collect { result ->
                when (result){
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        result.data?.let {
                            chargesList = it.toMutableStateList()
                        }
                    }
                    is Resource.Error -> {}
                }
            }
        }
    }
}
