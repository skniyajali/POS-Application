package com.niyaj.feature.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.data.repository.ReminderRepository
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    val state = reminderRepository.getAllReminders().mapLatest {
        if(it.isEmpty()) UiState.Empty else UiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    private val _selectedReminder = MutableStateFlow("")
    val selectedReminder = _selectedReminder.asStateFlow()

    fun onEvent(event: ReminderEvent) {
        when (event) {
            is ReminderEvent.SelectReminder -> {
                if (_selectedReminder.value == event.reminderId) {
                    _selectedReminder.value = ""
                } else {
                    _selectedReminder.value = event.reminderId
                }
            }

            is ReminderEvent.DeselectReminder -> {
                _selectedReminder.value = ""
            }

            is ReminderEvent.UpdateReminder -> {
                viewModelScope.launch {
                    reminderRepository.updateReminderAsNotCompleted(event.reminderId)
                }
            }

            is ReminderEvent.DeleteReminder -> {
                viewModelScope.launch {
                    reminderRepository.deleteReminder(event.reminderId)
                }
            }
        }
    }

}