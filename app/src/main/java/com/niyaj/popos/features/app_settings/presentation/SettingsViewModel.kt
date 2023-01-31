package com.niyaj.popos.features.app_settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.data_deletion.domain.use_cases.DataDeletionUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataDeletionUseCases: DataDeletionUseCases,
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
            when(val result = dataDeletionUseCases.deleteAllRecords()) {
                is Resource.Loading -> {
                    _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                }
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.OnSuccess("All records were successfully deleted"))
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to delete all records"))
                }
            }
        }
    }

    private fun deletePastData() {
        viewModelScope.launch {
            when(val result = dataDeletionUseCases.deleteData()) {
                is Resource.Loading -> {
                    _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                }
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.OnSuccess("Past records were successfully deleted"))
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to delete past records"))
                }
            }
        }
    }

}