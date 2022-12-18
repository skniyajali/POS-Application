package com.niyaj.popos.presentation.order

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.niyaj.popos.domain.use_cases.order.OrderUseCases
import com.niyaj.popos.domain.util.CartOrderType
import com.niyaj.popos.domain.util.OrderStatus
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.domain.util.filter_items.FilterOrder
import com.niyaj.popos.presentation.cart.dine_in.DineInState
import com.niyaj.popos.presentation.cart.dine_out.DineOutState
import com.niyaj.popos.util.Constants
import com.niyaj.popos.util.getCalculatedEndDate
import com.niyaj.popos.util.getCalculatedStartDate
import com.niyaj.popos.util.getEndTime
import com.niyaj.popos.util.getStartTime
import com.niyaj.popos.util.toFormattedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderUseCases: OrderUseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private lateinit var escposPrinter: EscPosPrinter

    private val _orders = mutableStateOf(OrderState())
    val orders : State<OrderState> = _orders

    private val _dineInOrders = MutableStateFlow(DineInState())
    val dineInOrders = _dineInOrders.asStateFlow()

    private val _dineOutOrders = MutableStateFlow(DineOutState())
    val dineOutOrders = _dineOutOrders.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    private val _selectedDate = MutableStateFlow("")
    val selectedDate = _selectedDate.asStateFlow()

    init {
        savedStateHandle.get<String>("selectedDate")?.let { selectedDate ->
            if(selectedDate.isNotEmpty() && selectedDate != LocalDate.now().toString()){
                onOrderEvent(OrderEvent.SelectDate(selectedDate))
            }else{
                getAllOrders()
            }
        }
    }

    fun onOrderEvent(event: OrderEvent){
        when (event){
            is OrderEvent.MarkedAsDelivered -> {
                viewModelScope.launch {
                    orderUseCases.changeOrderStatus(event.cartOrderId, OrderStatus.Delivered.orderStatus)
                    getAllOrders(FilterOrder.ByUpdatedDate(SortType.Descending))

//                    if(result){
//                        _eventFlow.emit(UiEvent.OnSuccess("Order Has Been Delivered"))
//                    }else{
//                        _eventFlow.emit(UiEvent.OnError("Unable To Mark Order As Delivered"))
//                    }
                }
            }

            is OrderEvent.MarkedAsProcessing -> {
                    viewModelScope.launch {
                        when(val result = orderUseCases.changeOrderStatus(event.cartOrderId, OrderStatus.Processing.orderStatus)){
                            is Resource.Loading -> {

                            }
                            is Resource.Success -> {
                                _eventFlow.emit(UiEvent.OnSuccess("Order Marked As Processing"))
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable To Mark Order As Processing"))
                            }
                        }
                    }
            }

            is OrderEvent.DeleteOrder -> {
                viewModelScope.launch {
                    if(event.cartOrderId.isNotEmpty()){
                        when(val result = orderUseCases.deleteOrder(event.cartOrderId)){
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                _eventFlow.emit(UiEvent.OnSuccess("Order Has Been Deleted successfully"))
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable To Delete Order"))
                            }
                        }
                    }else{
                        _eventFlow.emit(UiEvent.OnError("Unable To Delete Order"))
                    }
                }
            }

            is OrderEvent.OnFilterOrder -> {
                if(_orders.value.filterOrder::class == event.filterOrder::class &&
                   _orders.value.filterOrder.sortType == event.filterOrder.sortType
                ){
                    _orders.value = _orders.value.copy(
                        filterOrder = FilterOrder.ByUpdatedDate(SortType.Descending),
                    )
                    return
                }

                _orders.value = _orders.value.copy(
                    filterOrder = event.filterOrder,
                )
                getAllOrders(event.filterOrder)
            }

            is OrderEvent.OnSearchOrder -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)
                    getAllOrders(
                        FilterOrder.ByUpdatedDate(SortType.Descending),
                        event.searchText
                    )
                }
            }

            is OrderEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is OrderEvent.SelectDate -> {
                viewModelScope.launch {
                    val startDate = getCalculatedStartDate(date = event.date)
                    val endDate = getCalculatedEndDate(date = event.date)

                    getAllOrders(startDate = startDate, endDate = endDate)
                    _selectedDate.emit(startDate)
                }
            }

            is OrderEvent.PrintDeliveryReport -> {
                printDeliveryReport()
            }

            is OrderEvent.RefreshOrder -> {
                getAllOrders()
            }
        }
    }

    private fun getAllOrders(
        filterOrder: FilterOrder = _orders.value.filterOrder,
        searchText: String = "",
        startDate: String = getStartTime,
        endDate: String = getEndTime,
    ){
        viewModelScope.launch {
            orderUseCases.getAllOrders(filterOrder, searchText, startDate, endDate).collect { result ->
                when(result){
                    is Resource.Loading -> {
                        _orders.value = _orders.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _orders.value = _orders.value.copy(
                                orders = it,
                                filterOrder = filterOrder,
                            )

                            _dineInOrders.value = _dineInOrders.value.copy(
                                cartItems = it.filter { cart ->
                                    cart.cartOrder?.cartOrderType == CartOrderType.DineIn.orderType
                                }
                            )

                            _dineOutOrders.value = _dineOutOrders.value.copy(
                                cartItems = it.filter { cart ->
                                    cart.cartOrder?.cartOrderType  == CartOrderType.DineOut.orderType
                                }
                            )
                        }
                    }
                    is Resource.Error -> {
                        _orders.value = _orders.value.copy(
                            error =  result.message
                        )
                    }
                }
            }
        }
    }

    fun onSearchBarCloseAndClearClick(){
        viewModelScope.launch {
            onSearchTextClearClick()

            _toggledSearchBar.emit(false)

        }
    }

    fun onSearchTextClearClick(){
        viewModelScope.launch {
            _searchText.emit("")
            getAllOrders(
                FilterOrder.ByUpdatedDate(SortType.Descending),
                _searchText.value
            )
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

    private fun printDeliveryReport(){
        try {
            var printItems = ""

            printItems += getPrintableHeader()
            printItems += getPrintableOrders()

            connectPrinter()

            escposPrinter.printFormattedText(printItems, 50)
        }catch (e: Exception){
            Timber.e(e)
        }
    }

    private fun getPrintableHeader(): String {
        var header = "[C]<b><font size='big'>DELIVERY</font></b>\n\n"

        header += if (selectedDate.value.isEmpty()) {
            "[C]--------- ${System.currentTimeMillis().toString().toFormattedDate} --------\n"
        } else {
            "[C]----------${_selectedDate.value.toFormattedDate}---------\n"
        }

        header += "[L]\n"

        return header
    }

    private fun getPrintableOrders(): String {
        var orders = ""

        if(_dineOutOrders.value.cartItems.isNotEmpty()){
            val dineOutOrders = _dineOutOrders.value.cartItems.asReversed()

            orders += "[L]ID[Address][R]Price\n"
            orders += "[L]-------------------------------\n"

            dineOutOrders.forEach { cart ->
                if (cart.cartOrder != null){
                    orders += "[L]${cart.cartOrder.orderId.takeLast(3)}[C]${cart.cartOrder.address?.shortName}[R]${cart.orderPrice.first.minus(cart.orderPrice.second)}\n"
                    orders += "[L]-------------------------------\n"
                }
            }

        }else {
            orders += "[C]You have not place any order.\n"
        }

        orders += "[L]\n"

        return orders
    }
}