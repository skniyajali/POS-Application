package com.niyaj.popos.domain.use_cases.reports

import com.niyaj.popos.domain.repository.ReportsRepository
import com.niyaj.popos.domain.util.Resource

class DeletePastData(private val reportsRepository: ReportsRepository) {

    operator fun invoke(): Resource<Boolean> {
        return reportsRepository.deleteLastSevenDaysBeforeData()
    }
}