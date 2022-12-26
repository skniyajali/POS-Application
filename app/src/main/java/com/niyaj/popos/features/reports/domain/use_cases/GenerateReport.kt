package com.niyaj.popos.features.reports.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository

class GenerateReport(private val reportsRepository: ReportsRepository) {

    suspend operator fun invoke(startDate: String, endDate: String): Resource<Boolean> {
        return reportsRepository.generateReport(startDate, endDate)
    }
}