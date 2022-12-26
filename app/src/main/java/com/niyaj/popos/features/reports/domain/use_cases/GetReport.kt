package com.niyaj.popos.features.reports.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reports.domain.model.Reports
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository

class GetReport(private val reportsRepository: ReportsRepository) {

    operator fun invoke(startDate: String): Resource<Reports?> {
        return reportsRepository.getReport(startDate)
    }
}