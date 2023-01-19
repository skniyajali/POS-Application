package com.niyaj.popos.features.address.data.repository

import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.address.domain.repository.AddressValidationRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAddressRepository(): AddressRepository, AddressValidationRepository {

    private val _addresses = mutableListOf<Address>()

    override suspend fun getAllAddress(): Flow<Resource<List<Address>>> {
        return flow {
            emit(Resource.Success(_addresses))
        }
    }

    override suspend fun getAddressById(addressId: String): Resource<Address?> {
        return Resource.Success(_addresses.find { it.addressId == addressId })
    }

    override fun findAddressByName(addressName: String, addressId: String?): Boolean {
        val address = if (addressId.isNullOrEmpty()) {
            _addresses.find { it.addressName == addressName }
        }else {
            _addresses.find { it.addressId != addressId && it.addressName == addressName}
        }

        return address != null
    }

    override suspend fun addNewAddress(newAddress: Address): Resource<Boolean> {
        return Resource.Success(_addresses.add(newAddress))
    }

    override suspend fun updateAddress(newAddress: Address, addressId: String): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAddress(addressId: String): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override fun validateAddressName(addressName: String, addressId: String?): ValidationResult {
        if(addressName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Address name must not be empty",
            )
        }

        if(addressName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "The address name must be more than 2 characters long"
            )
        }

        val serverResult = findAddressByName(addressName, addressId)

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = "Address name already exists."
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateAddressShortName(addressShortName: String): ValidationResult {
        if(addressShortName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Address short name cannot be empty"
            )
        }

        if(addressShortName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "The short name must be more than 2 characters long"
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}