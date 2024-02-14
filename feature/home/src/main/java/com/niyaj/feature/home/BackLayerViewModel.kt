package com.niyaj.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.getStartTime
import com.niyaj.data.repository.ReportsRepository
import com.niyaj.model.Reports
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackLayerViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository
) : ViewModel() {

    private val _reportState = MutableStateFlow(Reports())
    val reportState = _reportState.asStateFlow()

    init {
        generateReport()
        getReport(getStartTime)
    }

    fun generateReport() {
        viewModelScope.launch {
            reportsRepository.generateReport(getStartTime)
        }
    }

    private fun getReport(startDate: String) {
        viewModelScope.launch {
            reportsRepository.getReport(startDate).collectLatest { result ->
                _reportState.value = result ?: Reports()
            }
        }
    }
}