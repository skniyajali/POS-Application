package com.niyaj.popos.features.reports.domain.reports

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository

class DeletePastData(private val reportsRepository: ReportsRepository) {

    operator fun invoke(): Resource<Boolean> {
        return reportsRepository.deleteLastSevenDaysBeforeData()
    }
}