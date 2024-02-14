package com.niyaj.app_settings

sealed class SettingsEvent {
    data object DeletePastRecords : SettingsEvent()

    data object DeleteAllRecords : SettingsEvent()
}
