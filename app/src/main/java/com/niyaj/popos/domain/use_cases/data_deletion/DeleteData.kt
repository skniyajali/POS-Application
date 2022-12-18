package com.niyaj.popos.domain.use_cases.data_deletion

import com.niyaj.popos.domain.repository.DataDeletionRepository
import com.niyaj.popos.domain.util.Resource

class DeleteData(private val dataDeletionRepository: DataDeletionRepository) {

    suspend operator fun invoke(): Resource<Boolean> {
        return dataDeletionRepository.deleteData()
    }
}