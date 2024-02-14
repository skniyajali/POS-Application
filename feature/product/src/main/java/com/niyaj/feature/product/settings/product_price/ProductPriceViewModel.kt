package com.niyaj.feature.product.settings.product_price

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.ProductRepository
import com.niyaj.data.repository.validation.ProductValidationRepository
import com.niyaj.model.Product
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.safeInt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class ProductPriceViewModel @Inject constructor(
    private val validationRepository: ProductValidationRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _selectedProducts = mutableStateListOf<String>()
    val selectedProducts: SnapshotStateList<String> = _selectedProducts

    private val _productPrice = mutableStateOf("")
    val productPrice: State<String> = _productPrice

    val priceError = snapshotFlow { _productPrice.value }.mapLatest {
        validationRepository.validateProductPrice(it.safeInt()).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    var onChoose by mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var count: Int = 0

    private var productCount = 0

    init {
        getAllProducts()
    }

    fun onEvent(event: ProductPriceEvent) {
        when (event) {
            is ProductPriceEvent.OnPriceChanged -> {
                _productPrice.value = event.productPrice
            }

            is ProductPriceEvent.OnChooseProduct -> {
                onChoose = !onChoose

                if (onChoose) {
                    getAllProducts()
                }
            }

            is ProductPriceEvent.SelectProduct -> {
                viewModelScope.launch {
                    if (_selectedProducts.contains(event.productId)) {
                        _selectedProducts.remove(event.productId)
                    } else {
                        _selectedProducts.add(event.productId)
                    }
                }

            }

            is ProductPriceEvent.SelectProducts -> {
                productCount += 1

                if (event.products.isNotEmpty()) {
                    viewModelScope.launch {
                        event.products.forEach { product ->
                            if (productCount % 2 != 0) {
                                val selectedProduct = _selectedProducts.find { it == product }

                                if (selectedProduct == null) {
                                    _selectedProducts.add(product)
                                }
                            } else {
                                _selectedProducts.remove(product)
                            }
                        }
                    }
                }
            }

            is ProductPriceEvent.SelectAllProduct -> {
                count += 1

                val products = _products.value

                if (products.isNotEmpty()) {
                    products.forEach { product ->
                        if (count % 2 != 0) {
                            val selectedProduct = _selectedProducts.find { it == product.productId }

                            if (selectedProduct == null) {
                                _selectedProducts.add(product.productId)
                            }
                        } else {
                            _selectedProducts.remove(product.productId)
                        }
                    }
                }
            }

            is ProductPriceEvent.DeselectProducts -> {
                _selectedProducts.clear()
            }

            is ProductPriceEvent.IncreaseProductPrice -> {
                val validationResult =
                    validationRepository.validateProductPrice(_productPrice.value.safeInt())

                if (validationResult.successful) {
                    viewModelScope.launch {
                        val result = productRepository.increasePrice(
                            _productPrice.value.safeInt(),
                            _selectedProducts.toList()
                        )

                        when (result) {
                            is Resource.Success -> {
                                val message = if (_selectedProducts.isEmpty()) {
                                    "All Products Price Has Been Increased"
                                } else "${_selectedProducts.size} Products Price Has Been Increased"

                                _eventFlow.emit(UiEvent.Success(message))
                            }

                            is Resource.Error -> {
                                _eventFlow.emit(
                                    UiEvent.Error(
                                        result.message ?: "Unable to Increase Product Price"
                                    )
                                )
                            }
                        }
                    }
                }
            }

            is ProductPriceEvent.DecreaseProductPrice -> {
                val validationResult =
                    validationRepository.validateProductPrice(productPrice.value.safeInt())

                if (validationResult.successful) {
                    viewModelScope.launch {
                        val result = productRepository.decreasePrice(
                            productPrice.value.safeInt(),
                            _selectedProducts.toList()
                        )

                        when (result) {
                            is Resource.Success -> {
                                val message = if (_selectedProducts.isEmpty()) {
                                    "All Products Price Has Been Decreased"
                                } else "${_selectedProducts.size} Products Price Has Been Decreased"

                                _eventFlow.emit(UiEvent.Success(message))
                            }

                            is Resource.Error -> {
                                _eventFlow.emit(
                                    UiEvent.Error(
                                        result.message ?: "Unable to Decrease Product Price"
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getAllProducts() {
        viewModelScope.launch {
            productRepository.getAllProducts("", "").collectLatest { result ->
                _products.value = result
            }
        }
    }
}