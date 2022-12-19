package com.niyaj.popos.realm.data_deletion.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.data_deletion.domain.repository.DataDeletionRepository

class DeleteData(private val dataDeletionRepository: DataDeletionRepository) {

    suspend operator fun invoke(): Resource<Boolean> {
        return dataDeletionRepository.deleteData()
    }
}