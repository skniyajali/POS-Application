package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.repository.DataDeletionRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.data_deletion.DataDeletionService

class DataDeletionRepositoryImpl(private val dataDeletionService: DataDeletionService) : DataDeletionRepository {

    override suspend fun deleteData(): Resource<Boolean> {
        return dataDeletionService.deleteData()
    }

    override suspend fun deleteAllRecords(): Resource<Boolean> {
        return dataDeletionService.deleteAllRecords()
    }
}