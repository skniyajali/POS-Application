package com.niyaj.popos.domain.use_cases.reports

import com.niyaj.popos.domain.model.Reports
import com.niyaj.popos.domain.repository.ReportsRepository
import com.niyaj.popos.domain.util.Resource

class GetReport(private val reportsRepository: ReportsRepository) {

    operator fun invoke(startDate: String): Resource<Reports?> {
        return reportsRepository.getReport(startDate)
    }
}