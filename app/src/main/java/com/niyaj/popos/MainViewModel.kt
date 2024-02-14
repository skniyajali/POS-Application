package com.niyaj.popos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.data.repository.AccountRepository
import com.niyaj.data.utils.NetworkMonitor
import com.niyaj.worker.status.WorkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    accountRepository: AccountRepository,
    connectivityObserver: NetworkMonitor,
    workMonitor: WorkMonitor,
) : ViewModel() {

    val isLoggedIn = accountRepository.checkIsLoggedIn()

    val networkStatus = connectivityObserver.isOnline.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        false
    )

    val reportState = workMonitor.isGeneratingReport.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        false,
    )

    val deleteState = workMonitor.isDeletingData.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        false,
    )

    val salaryReminderState = workMonitor.isDailySalaryReminderRunning.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        false,
    )

    val attendanceState = workMonitor.isAttendanceReminderRunning.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        false,
    )
}