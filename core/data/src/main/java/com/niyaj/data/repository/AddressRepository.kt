package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Address
import com.niyaj.model.AddressWiseOrder
import kotlinx.coroutines.flow.Flow

interface AddressRepository {

    suspend fun getAllAddress(searchText: String): Flow<List<Address>>

    suspend fun getAddressById(addressId: String): Resource<Address?>

    fun findAddressByName(addressName: String, addressId: String? = null): Boolean

    suspend fun addNewAddress(newAddress: Address): Resource<Boolean>

    suspend fun updateAddress(newAddress: Address, addressId: String): Resource<Boolean>

    suspend fun deleteAddress(addressId: String): Resource<Boolean>

    suspend fun deleteAddresses(addressIds: List<String>): Resource<Boolean>

    suspend fun deleteAllAddress(): Resource<Boolean>

    suspend fun importAddresses(addresses: List<Address>): Resource<Boolean>

    suspend fun getRecentOrdersOnAddress(addressId: String): Flow<List<AddressWiseOrder>>
}