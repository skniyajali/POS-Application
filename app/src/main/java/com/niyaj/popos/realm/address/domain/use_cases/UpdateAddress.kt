package com.niyaj.popos.realm.address.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.address.domain.model.Address
import com.niyaj.popos.realm.address.domain.repository.AddressRepository

class UpdateAddress(
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(newAddress: Address, addressId: String): Resource<Boolean>{
        return addressRepository.updateAddress(newAddress, addressId)
    }
}