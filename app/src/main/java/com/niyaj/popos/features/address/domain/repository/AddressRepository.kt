package com.niyaj.popos.features.address.domain.repository

import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.model.AddressWiseOrder
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.flow.Flow

interface AddressRepository {

    suspend fun getAllAddress(): Flow<Resource<List<Address>>>

    suspend fun getAddressById(addressId: String): Resource<Address?>

    fun findAddressByName(addressName: String, addressId: String? = null): Boolean

    suspend fun addNewAddress(newAddress: Address): Resource<Boolean>

    suspend fun updateAddress(newAddress: Address, addressId: String): Resource<Boolean>

    suspend fun deleteAddress(addressId: String): Resource<Boolean>

    suspend fun deleteAllAddress(): Resource<Boolean>

    suspend fun importAddresses(addresses: List<Address>): Resource<Boolean>

    suspend fun getRecentOrdersOnAddress(addressId: String): Flow<Resource<List<AddressWiseOrder>>>
}