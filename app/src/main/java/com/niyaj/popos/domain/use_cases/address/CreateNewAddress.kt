package com.niyaj.popos.domain.use_cases.address

import com.niyaj.popos.domain.model.Address
import com.niyaj.popos.domain.repository.AddressRepository
import com.niyaj.popos.domain.util.Resource

class CreateNewAddress(
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(address: Address): Resource<Boolean> {
        return addressRepository.addNewAddress(address)
    }
}