package com.niyaj.popos.features.app_settings.presentation

sealed class SettingsEvent {
    object DeletePastRecords: SettingsEvent()

    object DeleteAllRecords: SettingsEvent()
}
