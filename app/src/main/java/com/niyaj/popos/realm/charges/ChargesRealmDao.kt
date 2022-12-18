package com.niyaj.popos.realm.charges

import com.niyaj.popos.domain.model.Charges
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChargesRealmDao {

    suspend fun getAllCharges(): Flow<Resource<List<ChargesRealm>>>

    suspend fun getChargesById(chargesId: String): Resource<ChargesRealm?>

    fun findChargesByName(chargesName: String, chargesId: String?): Boolean

    suspend fun createNewCharges(newCharges: Charges): Resource<Boolean>

    suspend fun updateCharges(newCharges: Charges, chargesId: String): Resource<Boolean>

    suspend fun deleteCharges(chargesId: String): Resource<Boolean>
}