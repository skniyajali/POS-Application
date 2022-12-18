package com.niyaj.popos.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.niyaj.popos.domain.model.Reports
import com.niyaj.popos.domain.use_cases.reports.ReportsUseCases
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.util.Constants
import com.niyaj.popos.util.Constants.PRINT_PRODUCT_WISE_REPORT_LIMIT
import com.niyaj.popos.util.getCalculatedEndDate
import com.niyaj.popos.util.getCalculatedStartDate
import com.niyaj.popos.util.getEndTime
import com.niyaj.popos.util.getStartTime
import com.niyaj.popos.util.toFormattedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsUseCases: ReportsUseCases
) : ViewModel() {

    private lateinit var escposPrinter: EscPosPrinter

    private val _reportState = MutableStateFlow(Reports())
    val reportState = _reportState.asStateFlow()

    private val _reportsBarData = MutableStateFlow(ReportsBarState())
    val reportsBarData = _reportsBarData.asStateFlow()

    private val _productWiseData = MutableStateFlow(ProductWiseReportState())
    val productWiseData = _productWiseData.asStateFlow()

    private val _selectedDate = MutableStateFlow("")
    val selectedDate = _selectedDate.asStateFlow()

    val date = _selectedDate.value.ifEmpty { getStartTime }
    val endDate = getCalculatedEndDate(date = date)

    init {
        generateReport()
        getReport(date)
        getReportBarData(endDate)
        getProductWiseReport()
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
                }
            }

            is ReportsEvent.OnChangeOrderType -> {
                if (event.orderType != _productWiseData.value.orderType) {
                    val startDate =
                        if (_selectedDate.value.isEmpty()) getStartTime else getCalculatedStartDate(
                            date = _selectedDate.value
                        )
                    val endDate =
                        if (_selectedDate.value.isEmpty()) getEndTime else getCalculatedEndDate(date = _selectedDate.value)

                    getProductWiseReport(startDate, endDate, orderType = event.orderType)
                }
            }

            is ReportsEvent.PrintReport -> {
                printAllReports()
            }

            is ReportsEvent.RefreshReport -> {
                generateReport()
                getReport(date)
                getReportBarData(endDate)
                getProductWiseReport()
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
        startDate: String = getStartTime,
        endDate: String = getEndTime,
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

    private fun generateReport() {
        viewModelScope.launch {
            reportsUseCases.generateReport(getStartTime, getEndTime)
        }
    }

    private fun getReport(startDate: String) {
        viewModelScope.launch {
            val result = reportsUseCases.getReport(startDate)

            result.data?.let {
                _reportState.value = it
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

    private fun printAllReports() {
        try {
            var printItems = ""

            printItems += getPrintableHeader()
            printItems += getPrintableBoxReport()
            printItems += getPrintableBarReport()
            printItems += getPrintableProductWiseReport()

            connectPrinter()

            escposPrinter.printFormattedText(printItems, 50)
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
        val report = _reportState.value
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
                barReport += "[L]${data.yValue.toString()}[R]${data.xValue.toString().substringBefore(".")}\n"
            }
        } else {
            barReport += "[C]There is no data available."
        }

        barReport += "[L]-------------------------------\n\n"

        return barReport
    }

    private fun getPrintableProductWiseReport(): String {
        var productReport = "[C]TOP SALES PRODUCTS\n\n" +
                "[L]-------------------------------\n"

        if (_productWiseData.value.data.isNotEmpty()) {

            //TODO: use dynamic limit for printing products
            val productWiseData = _productWiseData.value.data.take(PRINT_PRODUCT_WISE_REPORT_LIMIT)

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

}