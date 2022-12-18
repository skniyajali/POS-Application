package com.niyaj.popos.presentation.product.settings.import_products

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.use_cases.product.ProductUseCases
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportProductViewModel @Inject constructor(
    private val productUseCases: ProductUseCases
) : ViewModel()  {

    private val _importedProducts = mutableStateListOf<Product>()
    val importedProducts : MutableList<Product> = _importedProducts

    private val _selectedProducts = mutableStateListOf<String>()
    val selectedProducts: SnapshotStateList<String> = _selectedProducts

    private val _selectedFileType = mutableStateOf(Constants.JSON_FILE_NAME)
    val selectedFileType : State<String> = _selectedFileType

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var onChoose by mutableStateOf(false)

    private var count: Int = 1

    private var productCount = 1

    fun onEvent(event: ImportProductEvent){
        when (event) {

            is ImportProductEvent.SelectProduct -> {
                viewModelScope.launch {
                    if(_selectedProducts.contains(event.productId)){
                        _selectedProducts.remove(event.productId)
                    }else{
                        _selectedProducts.add(event.productId)
                    }
                }

            }

            is ImportProductEvent.SelectProducts -> {
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

            is ImportProductEvent.SelectAllProduct -> {
                count += 1

                val products = _importedProducts.toList()

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

            is ImportProductEvent.DeselectProducts -> {
                _selectedProducts.clear()
            }

            is ImportProductEvent.OnChooseProduct -> {
                onChoose = !onChoose
            }


            is ImportProductEvent.ImportProductsData -> {
                _importedProducts.clear()

                if (event.products.isNotEmpty()) {
                    _importedProducts.addAll(event.products)
                    _selectedProducts.addAll(event.products.map { it.productId })
                }
            }

            is ImportProductEvent.ImportProducts -> {
                val products = mutableStateListOf<Product>()

                _selectedProducts.forEach {
                    val data = _importedProducts.find { product -> product.productId == it }
                    if (data != null) products.add(data)
                }

                viewModelScope.launch(Dispatchers.IO) {
                    when (val result = productUseCases.importProducts(products.toList())){
                        is Resource.Loading -> { }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("${products.toList().size} products imported successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to import products"))
                        }
                    }
                }
            }

            is ImportProductEvent.ClearImportedProducts -> {
                _importedProducts.clear()
                _selectedProducts.clear()
                onChoose = false
            }
        }
    }

}