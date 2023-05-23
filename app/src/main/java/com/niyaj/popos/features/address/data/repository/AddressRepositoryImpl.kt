package com.niyaj.popos.features.address.data.repository

import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.model.AddressWiseOrder
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.address.domain.repository.AddressValidationRepository
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class AddressRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AddressRepository, AddressValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Address Session:")
    }

    override suspend fun getAllAddress(): Flow<Resource<List<Address>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))
                    val items = realm.query<Address>().sort("addressId", Sort.DESCENDING).find()

                    val itemsFlow = items.asFlow()
                    itemsFlow.collect { changes: ResultsChange<Address> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }

                            else -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                        }
                    }

                } catch (e: Exception){
                    send(Resource.Loading(false))
                    send(Resource.Error(e.message ?: "Unable to get addresses", emptyList()))
                }
            }
        }
    }

    override suspend fun getAddressById(addressId: String): Resource<Address?> {
        return try {
            val address = realm.query<Address>("addressId == $0", addressId).first().find()

            Resource.Success(address)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get address", null)
        }
    }

    override fun findAddressByName(addressName: String, addressId: String?): Boolean {
        val address = if(addressId.isNullOrEmpty()) {
            realm.query<Address>("addressName == $0", addressName).first().find()
        }else {
            realm.query<Address>("addressId != $0 && addressName == $1", addressId, addressName).first().find()
        }

        return address != null
    }

    override suspend fun addNewAddress(newAddress: Address): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val validateAddressName = validateAddressName(newAddress.addressName, null)
                val validateAddressShortName = validateAddressShortName(newAddress.shortName)

                val hasError = listOf(validateAddressName, validateAddressShortName).any { !it.successful}

                if (!hasError) {
                    val address = Address()
                    address.addressId = newAddress.addressId.ifEmpty { BsonObjectId().toHexString() }
                    address.shortName = newAddress.shortName
                    address.addressName = newAddress.addressName
                    address.createdAt = newAddress.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        this.copyToRealm(address)
                    }

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to create address", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to new address", false)
        }
    }

    override suspend fun updateAddress(newAddress: Address, addressId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val validateAddressName = validateAddressName(newAddress.addressName, addressId)
                val validateAddressShortName = validateAddressShortName(newAddress.shortName)

                val hasError = listOf(validateAddressName, validateAddressShortName).any { !it.successful}

                if (!hasError) {
                    val address = realm.query<Address>("addressId == $0", addressId).first().find()
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
                        Resource.Error("Unable to find address", false)
                    }
                }else {
                    Resource.Error("Unable to update address", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update address", false)
        }
    }

    override suspend fun deleteAddress(addressId: String): Resource<Boolean> {
        return try {
            val address = realm.query<Address>("addressId == $0", addressId).first().find()

            if (address != null) {
                withContext(ioDispatcher) {
                    realm.write {
                        val cartOrder = this.query<CartOrder>("address.addressId == $0", addressId).find()
                        val cart = this.query<CartRealm>("cartOrder.address.addressId == $0", addressId).find()

                        if (cartOrder.isNotEmpty()){
                            delete(cartOrder)
                        }

                        if (cart.isNotEmpty()){
                            delete(cart)
                        }

                        findLatest(address)?.let {
                            delete(it)
                        }
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Unable to delete address", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete address", false)
        }
    }

    override suspend fun deleteAllAddress() : Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                realm.write {
                    val addresses = this.query<Address>().find()

                    if (addresses.isNotEmpty()) {
                        addresses.forEach { address ->
                            val cartOrder = this.query<CartOrder>("address.addressId == $0", address.addressId).find()
                            val cart = this.query<CartRealm>("cartOrder.address.addressId == $0", address.addressId).find()

                            if (cartOrder.isNotEmpty()){
                                delete(cartOrder)
                            }

                            if (cart.isNotEmpty()){
                                delete(cart)
                            }
                        }.also {
                            delete(addresses)
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception){
            Resource.Error(e.message?: "Unable to delete all addresses", false)
        }
    }

    override suspend fun importAddresses(addresses : List<Address>) : Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    addresses.forEach { address ->
                        val findAddress = this.query<Address>(
                            "addressId == $0 OR addressName == $1 OR shortName == $2",
                            address.addressId,
                            address.addressName,
                            address.shortName
                        ).first().find()

                        if (findAddress == null) {
                            val newAddress = Address()
                            newAddress.addressId = address.addressId.ifEmpty { BsonObjectId().toHexString() }
                            newAddress.shortName = address.shortName
                            newAddress.addressName = address.addressName
                            newAddress.createdAt = address.createdAt.ifEmpty { System.currentTimeMillis().toString() }
                            newAddress.updatedAt = System.currentTimeMillis().toString()

                            this.copyToRealm(newAddress)
                        }
                    }
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message?: "Unable to import addresses", false)
        }
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

        val serverResult = this.findAddressByName(addressName, addressId)

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

    override suspend fun getRecentOrdersOnAddress(addressId : String) : Flow<Resource<List<AddressWiseOrder>>> {
        return channelFlow {
            try {
                val orders = realm.query<CartOrder>("address.addressId == $0", addressId)
                    .sort("updatedAt", Sort.DESCENDING)
                    .asFlow()

                orders.collectLatest { result ->
                    when(result) {
                        is InitialResults -> {
                            send(Resource.Success(mapOrderToAddressWiseOrder(result.list)))
                            send(Resource.Loading(false))
                        }
                        is UpdatedResults -> {
                            send(Resource.Success(mapOrderToAddressWiseOrder(result.list)))
                            send(Resource.Loading(false))
                        }
                    }
                }
            }catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get recent orders on this address"))
            }
        }
    }

    private fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
        var totalPrice = 0
        var discountPrice = 0

        val cartOrder = realm.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
        val cartOrders = realm.query<CartRealm>("cartOrder.cartOrderId == $0", cartOrderId).find()

        if (cartOrder != null && cartOrders.isNotEmpty()) {
            if (cartOrder.doesChargesIncluded) {
                val charges = realm.query<Charges>().find()
                for (charge in charges) {
                    if (charge.isApplicable && cartOrder.orderType != CartOrderType.DineIn.orderType) {
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

        return Pair(totalPrice, discountPrice)
    }

    private fun mapOrderToAddressWiseOrder(data: List<CartOrder>): List<AddressWiseOrder> {
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