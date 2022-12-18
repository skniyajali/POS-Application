package com.niyaj.popos.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.use_cases.app_settings.SettingsUseCases
import com.niyaj.popos.domain.use_cases.data_deletion.DataDeletionUseCases
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCases: SettingsUseCases,
    private val dataDeletionUseCases: DataDeletionUseCases,
): ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun onEvent(event: SettingsEvent){
        when(event){
            is SettingsEvent.DeleteAllRecords -> {
                deleteAllRecords()
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

}