package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.Charges
import com.niyaj.popos.domain.repository.ChargesRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.charges.ChargesRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChargesRepositoryImpl(
    private val chargesRealmDao: ChargesRealmDao
) : ChargesRepository {

    override suspend fun getAllCharges(): Flow<Resource<List<Charges>>> {
        return flow {
            chargesRealmDao.getAllCharges().collect{ result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.map { chargesItem ->
                                Charges(
                                    chargesId = chargesItem._id,
                                    chargesName = chargesItem.chargesName,
                                    chargesPrice = chargesItem.chargesPrice,
                                    isApplicable = chargesItem.isApplicable,
                                    createdAt = chargesItem.created_at,
                                    updatedAt = chargesItem.updated_at,
                                )
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get charges items from database"))
                    }
                }
            }
        }
    }

    override suspend fun getChargesById(chargesId: String): Resource<Charges?> {
        val result = chargesRealmDao.getChargesById(chargesId)

        return result.data?.let { chargesItem ->
            Resource.Success(
                data = Charges(
                    chargesId = chargesItem._id,
                    chargesName = chargesItem.chargesName,
                    chargesPrice = chargesItem.chargesPrice,
                    isApplicable = chargesItem.isApplicable,
                    createdAt = chargesItem.created_at,
                    updatedAt = chargesItem.updated_at,
                )
            )
        } ?: Resource.Error(result.message ?: "Could not get charges item")
    }

    override fun findChargesByName(chargesName: String, chargesId: String?): Boolean {
        return chargesRealmDao.findChargesByName(chargesName, chargesId)
    }

    override suspend fun createNewCharges(newCharges: Charges): Resource<Boolean> {
        return chargesRealmDao.createNewCharges(newCharges)
    }

    override suspend fun updateCharges(newCharges: Charges, chargesId: String): Resource<Boolean> {
        return chargesRealmDao.updateCharges(newCharges, chargesId)
    }

    override suspend fun deleteCharges(chargesId: String): Resource<Boolean> {
        return chargesRealmDao.deleteCharges(chargesId)
    }
}