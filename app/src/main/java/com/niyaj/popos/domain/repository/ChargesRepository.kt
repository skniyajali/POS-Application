package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.model.Charges
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChargesRepository {

    suspend fun getAllCharges(): Flow<Resource<List<Charges>>>

    suspend fun getChargesById(chargesId: String): Resource<Charges?>

    fun findChargesByName(chargesName: String, chargesId: String?): Boolean

    suspend fun createNewCharges(newCharges: Charges): Resource<Boolean>

    suspend fun updateCharges(newCharges: Charges, chargesId: String): Resource<Boolean>

    suspend fun deleteCharges(chargesId: String): Resource<Boolean>
}