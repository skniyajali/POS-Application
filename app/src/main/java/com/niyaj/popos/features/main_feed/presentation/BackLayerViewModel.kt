package com.niyaj.popos.features.main_feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import com.niyaj.popos.features.reports.presentation.ReportState
import com.niyaj.popos.utils.getEndTime
import com.niyaj.popos.utils.getStartTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackLayerViewModel @Inject constructor(
    private val reportsRepository : ReportsRepository
): ViewModel() {

    private val _reportState = MutableStateFlow(ReportState())
    val reportState = _reportState.asStateFlow()

    init {
        generateReport()
        getReport(getStartTime)
    }

    fun generateReport() {
        viewModelScope.launch(Dispatchers.IO) {
            reportsRepository.generateReport(getStartTime, getEndTime)
        }
    }

    private fun getReport(startDate: String) {
        reportsRepository.getReport(startDate).onEach { result ->
            when(result){
                is Resource.Loading -> {
                    _reportState.value = _reportState.value.copy(
                        isLoading = result.isLoading
                    )
                }
                is Resource.Success -> {
                    result.data?.let {
                        _reportState.value = _reportState.value.copy(
                            report = it
                        )
                    }
                }
                is Resource.Error -> {
                    _reportState.value = _reportState.value.copy(
                        hasError = result.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}