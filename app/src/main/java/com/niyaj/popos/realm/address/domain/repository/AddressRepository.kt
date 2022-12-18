package com.niyaj.popos.realm.address.domain.repository

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.address.domain.model.Address
import kotlinx.coroutines.flow.Flow

interface AddressRepository {

    suspend fun getAllAddress(): Flow<Resource<List<Address>>>

    suspend fun getAddressById(addressId: String): Resource<Address?>

    suspend fun addNewAddress(newAddress: Address): Resource<Boolean>

    suspend fun updateAddress(newAddress: Address, addressId: String): Resource<Boolean>

    suspend fun deleteAddress(addressId: String): Resource<Boolean>

}