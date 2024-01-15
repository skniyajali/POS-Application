package com.niyaj.popos.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.account.domain.repository.AccountRepository
import com.niyaj.popos.features.network_connectivity.domain.model.ConnectivityStatus
import com.niyaj.popos.features.network_connectivity.domain.provider.ConnectivityObserver
import com.niyaj.popos.worker.status.WorkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    accountRepository : AccountRepository,
    connectivityObserver : ConnectivityObserver,
    workMonitor: WorkMonitor,
): ViewModel(){

    val isLoggedIn = accountRepository.checkIsLoggedIn()

    val networkStatus = connectivityObserver.observeConnectivity().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ConnectivityStatus.Unavailable
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