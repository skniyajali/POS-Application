package com.niyaj.popos.features.order.presentation.print_order

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.cart.domain.model.CartProductItem
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.domain.use_cases.GetAllCharges
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.order.domain.repository.OrderRepository
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoRepository
import com.niyaj.popos.utils.Constants
import com.niyaj.popos.utils.Constants.PRINT_LOGO
import com.niyaj.popos.utils.createDottedString
import com.niyaj.popos.utils.toFormattedTime
import com.niyaj.popos.utils.toRupee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class PrintViewModel @Inject constructor(
    private val orderUseCases: OrderRepository,
    private val getAllCharges : GetAllCharges,
    private val repository: RestaurantInfoRepository,
    application : Application,
) : ViewModel() {

    private lateinit var escposPrinter: EscPosPrinter
    private var chargesList = mutableStateListOf<Charges>()


    private var resInfo by mutableStateOf(RestaurantInfo())
    private val resLogo = application.applicationContext.getDrawable(PRINT_LOGO.toInt())

    init {
        getAllCharges()
        getProfileInfo()
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
                printItems += printProductDetails(itemDetails.orderedProducts)

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
            }
        }
    }

    private fun printRestaurantDetails(): String {
        val imagePrint = PrinterTextParserImg.bitmapToHexadecimalString(escposPrinter, resLogo)

        var details = "[C]<img>$imagePrint</img>\n\n"

//        details += "[C]<b><font size='big'>${resInfo.name}</font></b>\n"
//        details += "[C]-${resInfo.tagline}-\n\n"
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
            val productName = createDottedString(it.productName)

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
        return "[C]Pay by scanning this QR code\n\n"+
                "[L]\n" +
                "[C]<qrcode size ='40'>${resInfo.paymentQrCode}</qrcode>\n\n\n" +
                "[C]Good Food, Good Mood\n\n" +
                "[L]-------------------------------\n"
    }

    private fun printFooterInfo(): String {
        return "[C]Thank you for ordering!\n" +
                "[C]For order and inquiry, Call.\n" +
                "[C]${resInfo.primaryPhone} / ${resInfo.secondaryPhone}\n\n"
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
            getAllCharges.invoke().collect { result ->
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

    private fun getProfileInfo() {
        viewModelScope.launch {
            repository.getRestaurantInfo().collectLatest { result ->
                when (result){
                    is Resource.Success -> {
                        result.data?.let {
                            resInfo = it
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
