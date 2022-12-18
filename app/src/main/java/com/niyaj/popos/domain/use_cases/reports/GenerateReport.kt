package com.niyaj.popos.domain.use_cases.reports

import com.niyaj.popos.domain.repository.ReportsRepository
import com.niyaj.popos.domain.util.Resource

class GenerateReport(private val reportsRepository: ReportsRepository) {

    suspend operator fun invoke(startDate: String, endDate: String): Resource<Boolean> {
        return reportsRepository.generateReport(startDate, endDate)
    }
}