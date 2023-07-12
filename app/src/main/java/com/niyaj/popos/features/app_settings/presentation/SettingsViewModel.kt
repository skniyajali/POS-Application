package com.niyaj.popos.features.app_settings.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.app_settings.data.repository.BackupRestoreService
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.data_deletion.domain.repository.DataDeletionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataDeletionRepository: DataDeletionRepository,
    private val backupRestoreService : BackupRestoreService,
): ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun onEvent(event: SettingsEvent){
        when(event){
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
            when(val result = dataDeletionRepository.deleteAllRecords()) {
                is Resource.Loading -> {
                    _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                }
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
            when(val result = dataDeletionRepository.deleteData()) {
                is Resource.Loading -> {
                    _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                }
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.Success("Past records were successfully deleted"))
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error("Unable to delete past records"))
                }
            }
        }
    }

    fun backupFiles() {
        backupRestoreService.backup()
    }

    fun restoreFiles(context: Context) {
        backupRestoreService.restore()

        val packageManager: PackageManager = context.packageManager
        val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
        val componentName: ComponentName = intent.component!!
        val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(restartIntent)
        Runtime.getRuntime().exit(0)
    }

}