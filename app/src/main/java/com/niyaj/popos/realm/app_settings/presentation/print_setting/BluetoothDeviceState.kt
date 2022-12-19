package com.niyaj.popos.realm.app_settings.presentation.print_setting

data class BluetoothDeviceState(
    val name: String? = "",
    val address: String? = "",
    val bondState: Int? = null,
    val type: Int? = null,
    val connected: Boolean = false,
)
