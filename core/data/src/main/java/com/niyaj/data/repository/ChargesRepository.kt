package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Charges
import kotlinx.coroutines.flow.Flow

interface ChargesRepository {

    suspend fun getAllCharges(searchText: String): Flow<List<Charges>>

    suspend fun getChargesById(chargesId: String): Resource<Charges?>

    suspend fun findChargesByName(chargesName: String, chargesId: String?): Boolean

    suspend fun createOrUpdateCharges(newCharges: Charges, chargesId: String): Resource<Boolean>

    suspend fun deleteAllCharges(chargesIds: List<String>): Resource<Boolean>
}