package com.niyaj.popos.features.printer_info.domain.model

data class BluetoothDeviceState(
    val name: String = "",
    val address: String = "",
    val bondState: Int? = null,
    val type: Int? = null,
    val connected: Boolean = false,
)
