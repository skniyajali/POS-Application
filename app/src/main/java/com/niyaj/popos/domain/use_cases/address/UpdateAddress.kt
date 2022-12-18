package com.niyaj.popos.domain.use_cases.address

import com.niyaj.popos.domain.model.Address
import com.niyaj.popos.domain.repository.AddressRepository
import com.niyaj.popos.domain.util.Resource

class UpdateAddress(
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(newAddress: Address, addressId: String): Resource<Boolean>{
        return addressRepository.updateAddress(newAddress, addressId)
    }
}