package com.niyaj.popos.realm.address

import com.niyaj.popos.domain.model.Address
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AddressRealmDao {

    suspend fun getAllAddress(): Flow<Resource<List<AddressRealm>>>

    suspend fun getAddressById(addressId: String): Resource<AddressRealm?>

    suspend fun addNewAddress(newAddress: Address): Resource<Boolean>

    suspend fun updateAddress(newAddress: Address, addressId: String): Resource<Boolean>

    suspend fun deleteAddress(addressId: String): Resource<Boolean>

}