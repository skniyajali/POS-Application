package com.niyaj.popos.features.data_deletion.domain.repository

import com.niyaj.popos.features.common.util.Resource

interface DataDeletionRepository {

    /**
     * ## Prerequisites
    -  Delete **CartOrder** data before today date.
    -  Delete **Cart** data before today start date.
    -  Generate Report Before Deleting Data
     * @return [Resource] of [Boolean] type
     */
    suspend fun deleteData(): Resource<Boolean>

    /**
     * This method will clean up all database records including today data.
     * @return [Resource] of [Boolean] type
     */
    suspend fun deleteAllRecords(): Resource<Boolean>
}