package com.niyaj.popos.features.data_deletion.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.data_deletion.domain.repository.DataDeletionRepository

class DeleteAllRecords(private val dataDeletionRepository: DataDeletionRepository) {

    suspend operator fun invoke(): Resource<Boolean> {
        return dataDeletionRepository.deleteAllRecords()
    }
}