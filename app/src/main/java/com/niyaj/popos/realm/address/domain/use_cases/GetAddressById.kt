package com.niyaj.popos.realm.address.domain.use_cases

import com.niyaj.popos.realm.address.domain.model.Address
import com.niyaj.popos.realm.address.domain.repository.AddressRepository
import com.niyaj.popos.domain.util.Resource

class GetAddressById(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(addressId: String): Resource<Address?>{
        return addressRepository.getAddressById(addressId)
    }
}