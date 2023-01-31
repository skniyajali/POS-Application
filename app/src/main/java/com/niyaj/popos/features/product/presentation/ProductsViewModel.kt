package com.niyaj.popos.features.product.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.category.domain.use_cases.CategoryUseCases
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.product.domain.use_cases.ProductUseCases
import com.niyaj.popos.features.product.domain.util.FilterProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productUseCases: ProductUseCases,
    private val categoryUseCases: CategoryUseCases,
): ViewModel() {

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    private val _products = MutableStateFlow(ProductsState())
    val products = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(listOf())
    val categories = _categories.asStateFlow()

    private val _selectedProducts = mutableStateListOf<String>()
    val selectedProducts: SnapshotStateList<String> = _selectedProducts

    private val _selectedCategory = mutableStateOf("")
    val selectedCategory: State<String> = _selectedCategory

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var count: Int = 0

    private var productCount = 0

    init {
        getAllProducts(
            filterProduct = FilterProduct.ByCategoryId(SortType.Ascending)
        )

        getAllCategories()
    }

    fun onProductEvent(event: ProductEvent) {
        when(event){

            is ProductEvent.SelectProduct -> {
                viewModelScope.launch {
                    if(_selectedProducts.contains(event.productId)){
                        _selectedProducts.remove(event.productId)
                    }else{
                        _selectedProducts.add(event.productId)
                    }
                }
            }

            is ProductEvent.SelectProducts -> {
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

            is ProductEvent.SelectAllProduct -> {
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

            is ProductEvent.DeselectProducts -> {
                _selectedProducts.removeAll(_selectedProducts.toList())
            }

            is ProductEvent.DeleteProducts -> {
                viewModelScope.launch {
                    if (event.products.isNotEmpty()){
                        event.products.forEach { product ->
                            when (val result = productUseCases.deleteProduct(product)){
                                is Resource.Loading -> {}
                                is Resource.Success -> {
                                    _eventFlow.emit(UiEvent.OnSuccess("Product Deleted Successfully"))
                                }
                                is Resource.Error -> {
                                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete product"))
                                }
                            }
                            _selectedProducts.remove(product)
                        }
                    }
                }
            }

            is ProductEvent.OnFilterProduct -> {
                if(products.value.filterProduct::class == event.filterProduct::class &&
                    products.value.filterProduct.sortType == event.filterProduct.sortType
                ){
                    _products.value = _products.value.copy(
                        filterProduct = FilterProduct.ByProductId(SortType.Ascending)
                    )
                    return
                }
                _products.value = _products.value.copy(
                    filterProduct = event.filterProduct
                )
                getAllProducts(event.filterProduct)
            }

            is ProductEvent.OnSearchProduct -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)
                    getAllProducts(
                        _products.value.filterProduct,
                        event.searchText
                    )
                }
            }

            is ProductEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is ProductEvent.SelectCategory -> {
                if(event.categoryId == _selectedCategory.value){

                    _selectedCategory.value = ""

                    getAllProducts(_products.value.filterProduct, _searchText.value)
                }else{
                    getAllProducts(_products.value.filterProduct, _searchText.value, event.categoryId)

                    _selectedCategory.value = event.categoryId
                }
            }

            is ProductEvent.RefreshProduct -> {
                getAllProducts(_products.value.filterProduct)
            }
        }
    }

    private fun getAllProducts(filterProduct: FilterProduct, searchText:String = "", selectedCategory:String =_selectedCategory.value){
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

    fun onSearchBarCloseAndClearClick(){
        viewModelScope.launch {
            onSearchTextClearClick()
            _toggledSearchBar.emit(false)
        }
    }

    fun onSearchTextClearClick(){
        viewModelScope.launch {
            _searchText.emit("")
            getAllProducts(
                FilterProduct.ByProductId(SortType.Ascending),
                _searchText.value
            )
        }
    }

    private fun getAllCategories(){
        viewModelScope.launch {
            categoryUseCases.getAllCategories().collect { result ->
                when (result){
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _categories.emit(it)
                        }
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to load category"))
                    }
                }
            }
        }
    }

}