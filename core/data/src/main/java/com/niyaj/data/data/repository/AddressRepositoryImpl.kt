package com.niyaj.data.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.AddressRepository
import com.niyaj.data.repository.validation.AddressValidationRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.AddressEntity
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Address
import com.niyaj.model.AddressWiseOrder
import com.niyaj.model.OrderType
import com.niyaj.model.filterAddress
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class AddressRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher,
) : AddressRepository, AddressValidationRepository {

    val realm = Realm.open(config)

    override suspend fun getAllAddress(searchText: String): Flow<List<Address>> {
        return withContext(ioDispatcher) {
            realm.query<AddressEntity>()
                .sort("addressId", Sort.DESCENDING)
                .find()
                .asFlow()
                .mapLatest { items ->
                    items.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterAddress(searchText) },
                    )
                }
        }
    }

    override suspend fun getAddressById(addressId: String): Resource<Address?> {
        return try {
            val address = realm.query<AddressEntity>("addressId == $0", addressId).first().find()

            Resource.Success(address?.toExternalModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get address")
        }
    }

    override suspend fun findAddressByName(addressName: String, addressId: String?): Boolean {
        return withContext(ioDispatcher) {
            if (addressId.isNullOrEmpty()) {
                realm.query<AddressEntity>("addressName == $0", addressName).first().find()
            } else {
                realm.query<AddressEntity>(
                    "addressId != $0 && addressName == $1",
                    addressId,
                    addressName
                )
                    .first().find()
            } != null
        }
    }

    override suspend fun createOrUpdateAddress(
        newAddress: Address,
        addressId: String
    ): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val validateAddressName = validateAddressName(newAddress.addressName, addressId)
                val validateAddressShortName = validateAddressShortName(newAddress.shortName)

                val hasError =
                    listOf(validateAddressName, validateAddressShortName).any { !it.successful }

                if (!hasError) {
                    val address =
                        realm.query<AddressEntity>("addressId == $0", addressId).first().find()


                    if (address != null) {
                        realm.write {
                            findLatest(address)?.apply {
                                this.shortName = newAddress.shortName
                                this.addressName = newAddress.addressName
                                this.updatedAt = System.currentTimeMillis().toString()
                            }
                        }
                        Resource.Success(true)
                    } else {
                        realm.write {
                            this.copyToRealm(newAddress.toEntity())
                        }

                        Resource.Success(true)
                    }
                } else {
                    Resource.Error("Unable to update address")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to update address")
            }
        }
    }

    override suspend fun deleteAddresses(addressIds: List<String>): Resource<Boolean> {
        return try {
            addressIds.forEach { addressId ->
                val address = withContext(ioDispatcher) {
                    realm.query<AddressEntity>("addressId == $0", addressId).first().find()
                }

                if (address != null) {
                    withContext(ioDispatcher) {
                        realm.write {
                            val cartOrder = this
                                .query<CartOrderEntity>("address.addressId == $0", addressId)
                                .find()

                            val cart = this
                                .query<CartEntity>("cartOrder.address.addressId == $0", addressId)
                                .find()

                            if (cartOrder.isNotEmpty()) {
                                delete(cartOrder)
                            }

                            if (cart.isNotEmpty()) {
                                delete(cart)
                            }

                            findLatest(address)?.let {
                                delete(it)
                            }
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete address")
        }
    }

    override suspend fun deleteAllAddress(): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val addresses = this.query<AddressEntity>().find()

                    if (addresses.isNotEmpty()) {
                        addresses.forEach { address ->
                            val cartOrder =
                                this.query<CartOrderEntity>(
                                    "address.addressId == $0",
                                    address.addressId
                                )
                                    .find()
                            val cart = this.query<CartEntity>(
                                "cartOrder.address.addressId == $0",
                                address.addressId
                            ).find()

                            if (cartOrder.isNotEmpty()) {
                                delete(cartOrder)
                            }

                            if (cart.isNotEmpty()) {
                                delete(cart)
                            }
                        }.also {
                            delete(addresses)
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete all addresses")
        }
    }

    override suspend fun importAddresses(addresses: List<Address>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    addresses.forEach { address ->
                        val findAddress = this.query<AddressEntity>(
                            "addressId == $0 OR addressName == $1 OR shortName == $2",
                            address.addressId,
                            address.addressName,
                            address.shortName
                        ).first().find()

                        if (findAddress == null) {
                            val newAddress = AddressEntity()
                            newAddress.addressId =
                                address.addressId.ifEmpty { BsonObjectId().toHexString() }
                            newAddress.shortName = address.shortName
                            newAddress.addressName = address.addressName
                            newAddress.createdAt =
                                address.createdAt.ifEmpty { System.currentTimeMillis().toString() }
                            newAddress.updatedAt = System.currentTimeMillis().toString()

                            this.copyToRealm(newAddress)
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to import addresses")
        }
    }

    override suspend fun validateAddressName(
        addressName: String,
        addressId: String?
    ): ValidationResult {
        if (addressName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Address name must not be empty",
            )
        }

        if (addressName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "The address name must be more than 2 characters long"
            )
        }

        val serverResult = withContext(ioDispatcher) {
            this@AddressRepositoryImpl.findAddressByName(addressName, addressId)
        }

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
        if (addressShortName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Address short name cannot be empty"
            )
        }

        if (addressShortName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "The short name must be more than 2 characters long"
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override suspend fun getRecentOrdersOnAddress(addressId: String): Flow<List<AddressWiseOrder>> {
        return channelFlow {
            try {
                val orders = realm.query<CartOrderEntity>("address.addressId == $0", addressId)
                    .sort("updatedAt", Sort.DESCENDING)
                    .asFlow()

                orders.collectLatest { result ->
                    when (result) {
                        is InitialResults -> {
                            send(mapOrderToAddressWiseOrder(result.list))
                        }

                        is UpdatedResults -> {
                            send(mapOrderToAddressWiseOrder(result.list))
                        }
                    }
                }
            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    private suspend fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
        return withContext(ioDispatcher) {
            var totalPrice = 0
            var discountPrice = 0

            val cartOrder =
                realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()

            val cartOrders =
                realm.query<CartEntity>("cartOrder.cartOrderId == $0", cartOrderId).find()

            if (cartOrder != null && cartOrders.isNotEmpty()) {
                if (cartOrder.doesChargesIncluded) {
                    val charges = realm.query<ChargesEntity>().find()
                    for (charge in charges) {
                        if (charge.isApplicable && cartOrder.orderType != OrderType.DineIn.name) {
                            totalPrice += charge.chargesPrice
                        }
                    }
                }

                if (cartOrder.addOnItems.isNotEmpty()) {
                    for (addOnItem in cartOrder.addOnItems) {

                        totalPrice += addOnItem.itemPrice

                        if (!addOnItem.isApplicable) {
                            discountPrice += addOnItem.itemPrice
                        }
                    }
                }

                for (cartOrder1 in cartOrders) {
                    if (cartOrder1.product != null) {
                        totalPrice += cartOrder1.quantity.times(cartOrder1.product?.productPrice!!)
                    }
                }
            }

            Pair(totalPrice, discountPrice)
        }
    }

    private suspend fun mapOrderToAddressWiseOrder(data: List<CartOrderEntity>): List<AddressWiseOrder> {
        return data.map { order ->
            val price = countTotalPrice(order.cartOrderId)
            val totalPrice = price.first.minus(price.second).toString()

            AddressWiseOrder(
                cartOrderId = order.cartOrderId,
                orderId = order.orderId,
                customerPhone = order.customer?.customerPhone ?: "",
                totalPrice = totalPrice,
                updatedAt = order.updatedAt ?: order.createdAt,
                customerName = order.customer?.customerName
            )
        }
    }
}