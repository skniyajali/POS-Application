package com.niyaj.popos.domain.use_cases.address

import com.niyaj.popos.domain.repository.AddressRepository
import com.niyaj.popos.domain.util.Resource

class DeleteAddress(
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(addressId: String): Resource<Boolean> {
        return addressRepository.deleteAddress(addressId)
    }
}