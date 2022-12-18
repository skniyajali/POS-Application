package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.Address
import com.niyaj.popos.domain.repository.AddressRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.address.AddressRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddressRepositoryImpl(
    private val addressRealmDao: AddressRealmDao
) : AddressRepository {

    override suspend fun getAllAddress(): Flow<Resource<List<Address>>> {
        return flow {
            addressRealmDao.getAllAddress().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.map { address ->
                                Address(
                                    addressId = address._id,
                                    shortName = address.shortName,
                                    addressName = address.addressName,
                                    created_at = address.created_at,
                                    updated_at = address.updated_at,
                                )
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get addresses"))
                    }
                }
            }
        }
    }

    override suspend fun getAddressById(addressId: String): Resource<Address?> {
        val result =  addressRealmDao.getAddressById(addressId)

        return result.data?.let { address ->
            Resource.Success(
                Address(
                    addressId = address._id,
                    shortName = address.shortName,
                    addressName = address.addressName,
                    created_at = address.created_at,
                    updated_at = address.updated_at,
                )
            )
        } ?: Resource.Error(result.message ?: "Unable to get data from database")
    }

    override suspend fun addNewAddress(newAddress: Address): Resource<Boolean> {
        return addressRealmDao.addNewAddress(newAddress)
    }

    override suspend fun updateAddress(newAddress: Address, addressId: String): Resource<Boolean> {
        return addressRealmDao.updateAddress(newAddress, addressId)
    }

    override suspend fun deleteAddress(addressId: String): Resource<Boolean> {
        return addressRealmDao.deleteAddress(addressId)
    }

}