package com.niyaj.app_settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.DataDeletionRepository
import com.niyaj.database.utils.BackupRestoreService
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.restartApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataDeletionRepository: DataDeletionRepository,
    private val backupRestoreService: BackupRestoreService,
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.DeleteAllRecords -> {
                deleteAllRecords()
            }

            is SettingsEvent.DeletePastRecords -> {
                viewModelScope.launch {
                    deletePastData()
                }
            }
        }
    }

    private fun deleteAllRecords() {
        viewModelScope.launch {
            when (dataDeletionRepository.deleteAllRecords()) {
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.Success("All records were successfully deleted"))
                }

                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error("Unable to delete all records"))
                }
            }
        }
    }

    private fun deletePastData() {
        viewModelScope.launch {
            when (dataDeletionRepository.deleteData()) {
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.Success("Past records were successfully deleted"))
                }

                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error("Unable to delete past records"))
                }
            }
        }
    }

    fun backupDatabase() {
        viewModelScope.launch {
            when (val result = backupRestoreService.backupDatabase()) {
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.Success("Database backup successfully"))
                }

                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to backup database"))
                }
            }
        }
    }

    fun restoreDatabase(context: Context) {
        viewModelScope.launch {
            when (val result = backupRestoreService.restoreDatabase()) {
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.Success("Database restored successfully"))

                    delay(1000)

                    context.restartApplication()
                }

                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to restore database"))
                }
            }
        }
    }
}