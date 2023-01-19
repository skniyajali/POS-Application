package com.niyaj.popos.features.product.presentation.settings.product_price

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.util.safeString
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.product.domain.use_cases.ProductUseCases
import com.niyaj.popos.features.product.domain.use_cases.validation.ValidateProductPrice
import com.niyaj.popos.features.product.domain.util.FilterProduct
import com.niyaj.popos.features.product.presentation.ProductsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductPriceViewModel @Inject constructor(
    private val validateProductPrice: ValidateProductPrice,
    private val productUseCases: ProductUseCases,
): ViewModel() {

    private val _products = MutableStateFlow(ProductsState())
    val products = _products.asStateFlow()

    private val _selectedProducts = mutableStateListOf<String>()
    val selectedProducts: SnapshotStateList<String> = _selectedProducts

    private val _productPrice = mutableStateOf(ProductPriceState())
    val productPrice : State<ProductPriceState> = _productPrice

    var onChoose by mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var count: Int = 0

    private var productCount = 0


    fun onEvent(event: ProductPriceEvent){
        when (event){
            is ProductPriceEvent.OnPriceChanged -> {
                _productPrice.value = _productPrice.value.copy(
                    productPrice = event.productPrice
                )
            }

            is ProductPriceEvent.OnChooseProduct -> {
                onChoose = !onChoose

                if (onChoose) {
                    getAllProducts()
                }
            }

            is ProductPriceEvent.SelectProduct -> {
                viewModelScope.launch {
                    if(_selectedProducts.contains(event.productId)){
                        _selectedProducts.remove(event.productId)
                    }else{
                        _selectedProducts.add(event.productId)
                    }
                }

            }

            is ProductPriceEvent.SelectProducts -> {
                productCount += 1

                if (event.products.isNotEmpty()){
                    viewModelScope.launch {
                        event.products.forEach { product ->
                            if(productCount % 2 != 0){
                                val selectedProduct = _selectedProducts.find { it == product }

                                if (selectedProduct == null){
                                    _selectedProducts.add(product)
                                }
                            }else {
                                _selectedProducts.remove(product)
                            }
                        }
                    }
                }
            }

            is ProductPriceEvent.SelectAllProduct -> {
                count += 1

                val products = _products.value.products

                if (products.isNotEmpty()){
                    products.forEach { product ->
                        if (count % 2 != 0){
                            val selectedProduct = _selectedProducts.find { it == product.productId }

                            if (selectedProduct == null){
                                _selectedProducts.add(product.productId)
                            }
                        }else {
                            _selectedProducts.remove(product.productId)
                        }
                    }
                }
            }

            is ProductPriceEvent.DeselectProducts -> {
                _selectedProducts.clear()
            }

            is ProductPriceEvent.IncreaseProductPrice -> {

                val validationResult = validateProductPrice(safeString(_productPrice.value.productPrice), type = "increase")

                if (!validationResult.successful) {
                    _productPrice.value = _productPrice.value.copy(
                        productPriceError = validationResult.errorMessage
                    )
                }else{
                    viewModelScope.launch {
                        val result = productUseCases.increaseProductPrice(
                            safeString(_productPrice.value.productPrice),
                            _selectedProducts.toList()
                        )
                        
                        when(result){
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                _eventFlow.emit(
                                    UiEvent.OnSuccess(
                                    if (_selectedProducts.isEmpty()) "All Products Price Has Been Increased" else "${_selectedProducts.size} Products Price Has Been Increased"
                                ))
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to Increase Product Price"))
                            }
                        }
                    }
                }
            }

            is ProductPriceEvent.DecreaseProductPrice -> {

                val validationResult = validateProductPrice(safeString(_productPrice.value.productPrice), type = "decrease")

                if (!validationResult.successful) {
                    _productPrice.value = _productPrice.value.copy(
                        productPriceError = validationResult.errorMessage
                    )
                }else{
                    viewModelScope.launch {
                        val result = productUseCases.decreaseProductPrice(
                            safeString(_productPrice.value.productPrice),
                            _selectedProducts.toList()
                        )

                        when(result){
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                _eventFlow.emit(
                                    UiEvent.OnSuccess(
                                    if (_selectedProducts.isEmpty()) "All Products Price Has Been Decreased" else "${_selectedProducts.size} Products Price Has Been Decreased"
                                ))
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to Decrease Product Price"))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getAllProducts(
        filterProduct: FilterProduct = FilterProduct.ByCategoryId(SortType.Ascending),
        searchText: String = "",
        selectedCategory: String = "",
    ){
        viewModelScope.launch {
            productUseCases.getAllProducts(filterProduct = filterProduct, searchText, selectedCategory).collect { result ->
                when(result){
                    is Resource.Loading -> {
                        _products.value = _products.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {products ->
                            _products.value = _products.value.copy(
                                products = products,
                                filterProduct = filterProduct,
                            )
                        }
                    }
                    is Resource.Error -> {
                        _products.value = _products.value.copy(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

}

data class ProductPriceState(
    val productPrice: String = "",
    val productPriceError: String? = null,
)