package com.niyaj.popos.features.product.presentation.settings.export_products

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.product.domain.use_cases.ProductUseCases
import com.niyaj.popos.features.product.domain.util.FilterProduct
import com.niyaj.popos.features.product.presentation.ProductsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportProductViewModel @Inject  constructor(
    private val productUseCases: ProductUseCases
): ViewModel() {

    private val _products = MutableStateFlow(ProductsState())
    val products = _products.asStateFlow()

    private val _exportedProducts = MutableStateFlow<List<Product>>(emptyList())
    val exportedProducts = _exportedProducts.asStateFlow()

    private val _selectedProducts = mutableStateListOf<String>()
    val selectedProducts: SnapshotStateList<String> = _selectedProducts

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var onChoose by mutableStateOf(false)

    private var count: Int = 0

    private var productCount = 0

    init {
        getAllProducts()
    }

    fun onEvent(event: ExportProductEvent){
        when (event) {

            is ExportProductEvent.SelectProduct -> {
                viewModelScope.launch {
                    if(_selectedProducts.contains(event.productId)){
                        _selectedProducts.remove(event.productId)
                    }else{
                        _selectedProducts.add(event.productId)
                    }
                }

            }

            is ExportProductEvent.SelectProducts -> {
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

            is ExportProductEvent.SelectAllProduct -> {
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

            is ExportProductEvent.DeselectProducts -> {
                _selectedProducts.clear()
            }

            is ExportProductEvent.OnChooseProduct -> {
                onChoose = !onChoose
            }

            is ExportProductEvent.GetExportedProduct -> {
                viewModelScope.launch(Dispatchers.Main) {
                    if (_selectedProducts.isEmpty()){
                        _exportedProducts.emit(_products.value.products)
                    } else {
                        val products = mutableListOf<Product>()

                        _selectedProducts.forEach { id ->
                            val product = _products.value.products.find { it.productId == id }
                            if (product != null){
                                products.add(product)
                            }
                        }

                        _exportedProducts.emit(products.toList())
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
        viewModelScope.launch(Dispatchers.Main) {
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