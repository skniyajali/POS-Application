package com.niyaj.feature.product.settings.export_products

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.data.repository.ProductRepository
import com.niyaj.model.Product
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class ExportProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
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

    /**
     *
     */
    fun onEvent(event: ExportProductEvent) {
        when (event) {

            is ExportProductEvent.SelectProduct -> {
                viewModelScope.launch {
                    if (_selectedProducts.contains(event.productId)) {
                        _selectedProducts.remove(event.productId)
                    } else {
                        _selectedProducts.add(event.productId)
                    }
                }

            }

            is ExportProductEvent.SelectProducts -> {
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

            is ExportProductEvent.SelectAllProduct -> {
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

            is ExportProductEvent.DeselectProducts -> {
                _selectedProducts.clear()
            }

            is ExportProductEvent.OnChooseProduct -> {
                onChoose = !onChoose
            }

            is ExportProductEvent.GetExportedProduct -> {
                viewModelScope.launch {
                    if (_selectedProducts.isEmpty()) {
                        _exportedProducts.emit(_products.value)
                    } else {
                        val products = mutableListOf<Product>()

                        _selectedProducts.forEach { id ->
                            val product = _products.value.find { it.productId == id }
                            if (product != null) {
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
        searchText: String = "",
        selectedCategory: String = "",
    ) {
        viewModelScope.launch {
            productRepository.getAllProducts(searchText, selectedCategory).collectLatest { result ->
                _products.value = result
            }
        }
    }

}