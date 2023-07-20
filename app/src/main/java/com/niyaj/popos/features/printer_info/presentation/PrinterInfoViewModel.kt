package com.niyaj.popos.features.printer_info.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.printer_info.domain.repository.PrinterRepository
import com.niyaj.popos.features.printer_info.domain.utils.BluetoothPrinter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterInfoViewModel @Inject constructor(
    repository : PrinterRepository,
    private val bluetoothPrinter : BluetoothPrinter,
) : ViewModel() {

    val info : StateFlow<PrinterInfoState> = repository.getPrinterInfo()
        .map {
            if (it.printerId.isEmpty()) {
                PrinterInfoState.Empty
            } else {
                PrinterInfoState.Success(it)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            PrinterInfoState.Loading
        )

    val printers = bluetoothPrinter.getBluetoothPrinters().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun connectPrinter(address: String) {
        viewModelScope.launch {
            bluetoothPrinter.connectBluetoothPrinter(address)
        }
    }

    fun testPrint() {
        viewModelScope.launch {
            bluetoothPrinter.printTestData()
        }
    }
}