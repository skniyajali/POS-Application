package com.niyaj.popos.features.reports.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.common.utils.getCalculatedEndDate
import com.niyaj.popos.common.utils.getCalculatedStartDate
import com.niyaj.popos.common.utils.getEndTime
import com.niyaj.popos.common.utils.getStartTime
import com.niyaj.popos.common.utils.toFormattedDate
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.printer_info.domain.utils.BluetoothPrinter
import com.niyaj.popos.features.reports.domain.model.Reports
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import com.niyaj.popos.features.reports.domain.use_cases.ReportsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ReportsViewModel
 * @author Sk Niyaj Ali
 */
@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsUseCases: ReportsUseCases,
    private val reportsRepository : ReportsRepository,
    private val bluetoothPrinter : BluetoothPrinter,
) : ViewModel() {

    private val escposPrinter = bluetoothPrinter.printer
    private val info = bluetoothPrinter.info

    private val _reportState = MutableStateFlow(ReportState())
    val reportState = _reportState.asStateFlow()

    private val _reportsBarData = MutableStateFlow(ReportsBarState())
    val reportsBarData = _reportsBarData.asStateFlow()

    private val _productWiseData = MutableStateFlow(ProductWiseReportState())
    val productWiseData = _productWiseData.asStateFlow()

    private val _categoryWiseData = MutableStateFlow(CategoryWiseReportState())
    val categoryWiseData = _categoryWiseData.asStateFlow()

    private val _customerWiseData = MutableStateFlow(CustomerWiseReportState())
    val customerWiseData = _customerWiseData.asStateFlow()

    private val _addressWiseData = MutableStateFlow(AddressWiseReportState())
    val addressWiseData = _addressWiseData.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedDate = MutableStateFlow("")
    val selectedDate = _selectedDate.asStateFlow()

    val date = _selectedDate.value.ifEmpty { getStartTime }
    private val endTime = getCalculatedEndDate(date = date)

    init {
        generateReport()
        getReport(date)
        getReportBarData(endTime)
        getProductWiseReport()
        getCategoryWiseReport()
        getAddressWiseReport()
        getCustomerWiseReport()
    }

    fun onReportEvent(event: ReportsEvent) {
        when (event) {
            is ReportsEvent.SelectDate -> {
                viewModelScope.launch {
                    val startDate = getCalculatedStartDate(date = event.date)
                    val endDate = getCalculatedEndDate(date = event.date)

                    _selectedDate.emit(startDate)

                    getReportBarData(endDate)
                    getReport(startDate)
                    getProductWiseReport(startDate, endDate)
                    getCategoryWiseReport(startDate, endDate)
                    getAddressWiseReport(startDate, endDate)
                    getCustomerWiseReport(startDate, endDate)
                }
            }

            is ReportsEvent.OnChangeOrderType -> {
                if (event.orderType != _productWiseData.value.orderType) {
                    val startDate =  getCalculatedStartDate(date = _selectedDate.value.ifEmpty { getStartTime })
                    val endDate = getCalculatedEndDate(date = startDate)

                    getProductWiseReport(startDate, endDate, orderType = event.orderType)
                }
            }

            is ReportsEvent.OnChangeCategoryOrderType -> {
                if (event.orderType != _categoryWiseData.value.orderType) {
                    val startDate =  getCalculatedStartDate(date = _selectedDate.value.ifEmpty { getStartTime })
                    val endDate = getCalculatedEndDate(date = startDate)

                    getCategoryWiseReport(startDate, endDate, orderType = event.orderType)
                }
            }

            is ReportsEvent.PrintReport -> {
                printAllReports()
            }

            is ReportsEvent.RefreshReport -> {
                _selectedDate.value = ""
                generateReport()
                getReport(date)
                getReportBarData(endTime)
                getProductWiseReport()
                getCategoryWiseReport()
                getAddressWiseReport()
                getCustomerWiseReport()
            }

            is ReportsEvent.OnSelectCategory -> {
                viewModelScope.launch {
                    if (_selectedCategory.value == event.categoryName){
                        _selectedCategory.emit("")
                    }else {
                        _selectedCategory.emit(event.categoryName)
                    }
                }
            }

            is ReportsEvent.GenerateReport -> {
                generateReport()
            }
        }
    }

    private fun getReportBarData(selectedDate: String = "") {
        viewModelScope.launch {
            reportsUseCases.getReportsBarData(selectedDate).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _reportsBarData.value = _reportsBarData.value.copy(
                            isLoading = result.isLoading
                        )
                    }

                    is Resource.Success -> {
                        result.data?.let { reportBarData ->
                            _reportsBarData.value = _reportsBarData.value.copy(
                                reportBarData = reportBarData
                            )
                        }
                    }

                    is Resource.Error -> {
                        _reportsBarData.value = _reportsBarData.value.copy(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun getProductWiseReport(
        startDate: String = date,
        endDate: String = endTime,
        orderType: String = ""
    ) {
        viewModelScope.launch {
            reportsUseCases.getProductWiseReport(startDate, endDate, orderType)
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _productWiseData.value = _productWiseData.value.copy(
                                isLoading = result.isLoading
                            )
                        }

                        is Resource.Success -> {
                            result.data?.let { data ->
                                _productWiseData.value = _productWiseData.value.copy(
                                    data = data,
                                    orderType = orderType
                                )
                            }
                        }

                        is Resource.Error -> {
                            _productWiseData.value = _productWiseData.value.copy(
                                error = result.message
                            )
                        }
                    }
                }
        }
    }

    private fun getCategoryWiseReport(
        startDate: String = date,
        endDate: String = endTime,
        orderType: String = ""
    ) {
        viewModelScope.launch {
            reportsRepository.getProductWiseReport(startDate, endDate, orderType).collectLatest { result ->
                when (result){
                    is Resource.Loading -> {
                        _categoryWiseData.value = _categoryWiseData.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let { data ->
                            _categoryWiseData.value = _categoryWiseData.value.copy(
                                categoryWiseReport = data,
                                orderType = orderType,
                            )
                        }
                    }
                    is Resource.Error -> {
                        _categoryWiseData.value = _categoryWiseData.value.copy(
                            hasError = result.message
                        )
                    }
                }
            }
        }
    }

    private fun getCustomerWiseReport(
        startDate: String = date,
        endDate: String = endTime,
    ){
        reportsUseCases.getCustomerWiseReport(startDate, endDate).onEach { result ->

            when(result) {
                is Resource.Loading -> {
                    _customerWiseData.value = _customerWiseData.value.copy(
                        isLoading = result.isLoading
                    )
                }
                is Resource.Success -> {
                    result.data?.let { data ->
                        _customerWiseData.value = _customerWiseData.value.copy(
                            reports = data
                        )
                    }
                }
                is Resource.Error -> {
                    _customerWiseData.value = _customerWiseData.value.copy(
                        error = result.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getAddressWiseReport(
        startDate: String = date,
        endDate: String = endTime,
    ){
        reportsUseCases.getAddressWiseReport(startDate, endDate).onEach { result ->
            when(result) {
                is Resource.Loading -> {
                    _addressWiseData.value = _addressWiseData.value.copy(
                        isLoading = result.isLoading
                    )
                }
                is Resource.Success -> {
                    result.data?.let { data ->
                        _addressWiseData.value = _addressWiseData.value.copy(
                            reports = data
                        )
                    }
                }
                is Resource.Error -> {
                    _addressWiseData.value = _addressWiseData.value.copy(
                        error = result.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun generateReport() {
        viewModelScope.launch(Dispatchers.IO) {
            reportsRepository.generateReport(getStartTime, getEndTime)
        }
    }

    private fun getReport(startDate: String = date) {
        viewModelScope.launch {
            reportsRepository.getReport(startDate).collectLatest { result ->
                when(result){
                    is Resource.Loading -> {
                        _reportState.value = _reportState.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        val data = result.data ?: Reports()

                        _reportState.value = _reportState.value.copy(report = data)
                    }
                    is Resource.Error -> {
                        _reportState.value = _reportState.value.copy(hasError = result.message)
                    }
                }
            }
        }
    }

    private fun printAllReports() {
        try {
            var printItems = ""

            printItems += getPrintableHeader()
            printItems += getPrintableBoxReport()
            printItems += getPrintableBarReport()
            printItems += getPrintableCategoryWiseReport()
            printItems += getPrintableAddressWiseReport()
            printItems += getPrintableCustomerWiseReport()

            escposPrinter?.printFormattedText(printItems, info.printerWidth)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun getPrintableHeader(): String {
        var header = "[C]<b><font size='big'>REPORTS</font></b>\n\n"

        header += if (selectedDate.value.isEmpty()) {
            "[C]--------- ${System.currentTimeMillis().toString().toFormattedDate} --------\n"
        } else {
            "[C]----------${_selectedDate.value.toFormattedDate}---------\n"
        }

        header += "[L]\n"

        return header
    }

    private fun getPrintableBoxReport(): String {
        val report = _reportState.value.report
        val totalAmount = report.expensesAmount.plus(report.dineInSalesAmount).plus(report.dineOutSalesAmount).toString()

        var boxReport = "[C]TOTAL EXPENSES & SALES\n\n"
        boxReport += "[L]-------------------------------\n"
        boxReport += "[L]DineIn Sales(${report.dineInSalesQty})[R]${report.dineInSalesAmount}\n"
        boxReport += "[L]DineOut Sales(${report.dineOutSalesQty})[R]${report.dineOutSalesAmount}\n"
        boxReport += "[L]Expenses(${report.expensesQty})[R]${report.expensesAmount}\n"
        boxReport += "[L]-------------------------------\n"
        boxReport += "[L]Total - [R]${totalAmount}\n"
        boxReport += "[L]-------------------------------\n\n"


        return boxReport
    }

    private fun getPrintableBarReport(): String {
        var barReport = "[C]LAST ${_reportsBarData.value.reportBarData.size} DAYS REPORTS\n\n" +
                "[L]-------------------------------\n"

        if (_reportsBarData.value.reportBarData.isNotEmpty()) {
            val barData = _reportsBarData.value.reportBarData

            barData.forEach { data ->
                barReport += "[L]${data.yValue}[R]${data.xValue.toString().substringBefore(".")}\n"
            }
        } else {
            barReport += "[C]Reports not available. \n"
        }

        barReport += "[L]-------------------------------\n\n"

        return barReport
    }

    private fun getPrintableProductWiseReport(): String {
        var productReport = "[C]TOP SALES PRODUCTS\n\n"

        productReport += "[L]-------------------------------\n"

        if (_productWiseData.value.data.isNotEmpty()) {

            //TODO: use dynamic limit for printing products
            val productWiseData = _productWiseData.value.data.take(info.productWiseReportLimit)

            productReport += "[L]Name[R]Qty\n"
            productReport += "[L]-------------------------------\n"

            productWiseData.forEach { data ->
                productReport += "[L]${data.yValue}[R]${
                    data.xValue.toString().substringBefore(".")
                }\n"
            }
        } else {
            productReport += "[C]Product report is not available"
        }

        productReport += "[L]-------------------------------\n"

        return productReport
    }

    private fun getPrintableCategoryWiseReport(): String {
        var report = "[C]TOP SALES PRODUCTS\n\n"

        report += "[L]-------------------------------\n"


        val groupedByCategory = _categoryWiseData.value.categoryWiseReport.groupBy { it.product?.category?.categoryName }

        if (groupedByCategory.isNotEmpty()) {
            groupedByCategory.forEach { (category, products) ->
                if (category != null && products.isNotEmpty()) {
                    val totalQuantity = products.sumOf { it.quantity }.toString()

                    report += "[L]-------------------------------\n"
                    report += "[L]${category} [R]${totalQuantity}\n"
                    report += "[L]-------------------------------\n"

                    val sortedProducts = products.sortedByDescending { it.quantity}

                    sortedProducts.forEachIndexed { _, product ->
                        report += "[L]${product.product?.productName}[R]${product.quantity}\n"

//
//                        if (index != products.size - 1) {
//                            report += "[L]-------------------------------\n"
//                        }
                    }

                    report += "[L]-------------------------------\n"
                }
            }
        } else{
            report += "[C]Product Report Not Available \n"
        }

        report += "[L]-------------------------------\n\n"

        return report
    }

    private fun getPrintableAddressWiseReport(): String {
        var report = "[C]TOP DELIVERY PLACES\n\n"

        report += "[L]-------------------------------\n"

        val addresses = _addressWiseData.value.reports.take(info.addressWiseReportLimit)

        if (addresses.isNotEmpty()){
            addresses.forEachIndexed { _, address ->

                report += "[L]${address.address?.addressName} [R]${address.orderQty}\n"

//                if (index != addresses.size - 1){
//                    report += "[L]-------------------------------\n"
//                }
            }

        }else {
            report += "[C]Address Report Not Available \n"
        }

        report += "[L]-------------------------------\n\n"

        return report
    }

    private fun getPrintableCustomerWiseReport(): String {
        var report = "[C]MOST ORDERED CUSTOMERS\n\n"

        report += "[L]-------------------------------\n"

        val customers = _customerWiseData.value.reports.take(info.customerWiseReportLimit)

        if (customers.isNotEmpty()){
            customers.forEachIndexed { _, customerWiseReport ->
                val name = if (customerWiseReport.customer?.customerName != null) customerWiseReport.customer.customerName else ""

                report += "[L]${customerWiseReport.customer?.customerPhone}[C]${name?.take(12)} [R]${customerWiseReport.orderQty}\n"

//                if (index != customers.size -1){
//                    report += "[L]-------------------------------\n"
//                }
            }
        }else{
            report += "[C]Customer Report Not Available \n"
        }

        report += "[L]-------------------------------\n\n"

        return report
    }

}