package com.niyaj.popos.realm.app_settings.presentation

sealed class SettingsEvent {

    object DeleteAllRecords: SettingsEvent()
}
