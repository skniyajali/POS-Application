package com.niyaj.popos.features.main_feed.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.cart.domain.use_cases.CartUseCases
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.category.domain.util.FilterCategory
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.main_feed.domain.use_cases.MainFeedUseCases
import com.niyaj.popos.features.main_feed.presentation.components.category.MainFeedCategoryEvent
import com.niyaj.popos.features.main_feed.presentation.components.category.MainFeedCategoryState
import com.niyaj.popos.features.main_feed.presentation.components.product.MainFeedProductEvent
import com.niyaj.popos.features.main_feed.presentation.components.product.MainFeedProductState
import com.niyaj.popos.features.product.domain.util.FilterProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFeedViewModel @Inject constructor(
    private val mainFeedUseCases: MainFeedUseCases,
    private val cartUseCases: CartUseCases,
): ViewModel() {
    
    private val _selectedCategory = mutableStateOf("")
    val selectedCategory: State<String> = _selectedCategory

    private val _categories = MutableStateFlow(MainFeedCategoryState())
    val categories = _categories.asStateFlow()

    private val _products = MutableStateFlow(MainFeedProductState())
    val products = _products.asStateFlow()

    private val _selectedCartOrder = MutableStateFlow<CartOrder?>(null)
    val selectedCartOrder =  _selectedCartOrder.asStateFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getAllMainFeedProducts()
        getAllCategories()
        getSelectedCartOrder()
    }
    
    fun onMainFeedCategoryEvent(event: MainFeedCategoryEvent){
        when(event){
            is MainFeedCategoryEvent.OnFilterCategory -> {
                if(categories.value.filterCategory::class == event.filterCategory::class &&
                    categories.value.filterCategory.sortType == event.filterCategory.sortType
                ){
                    return
                }

                getAllCategories(event.filterCategory)
            }

            is MainFeedCategoryEvent.OnSelectCategory -> {
                if(event.categoryId == _selectedCategory.value){
                    _selectedCategory.value = ""
                    getAllMainFeedProducts(_products.value.filterProduct, _selectedCategory.value, _searchText.value)
                }else{
                    getAllMainFeedProducts(_products.value.filterProduct, event.categoryId, _searchText.value)
                    _selectedCategory.value = event.categoryId
                }
            }
        }
    }

    fun onMainFeedProductEvent(event: MainFeedProductEvent){
        when(event){
            is MainFeedProductEvent.OnFilterProduct -> {
                if(products.value.filterProduct::class == event.filterProduct::class &&
                    products.value.filterProduct.sortType == event.filterProduct.sortType
                ){
                    _products.value = products.value.copy(
                        filterProduct = FilterProduct.ByCategoryId(SortType.Ascending)
                    )
                    return
                }

                _products.value = products.value.copy(
                    filterProduct = event.filterProduct
                )

                getAllMainFeedProducts(event.filterProduct, _selectedCategory.value, _searchText.value)
            }

            is MainFeedProductEvent.SearchProduct -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)
                    getAllMainFeedProducts(
                        _products.value.filterProduct,
                        _selectedCategory.value,
                        event.searchText
                    )
                }
            }

            is MainFeedProductEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

//            is MainFeedProductEvent.AddProductToCart -> {
//                viewModelScope.launch {
//                    if (event.cartOrderId.isEmpty()) {
//                        _eventFlow.emit(UiEvent.OnError("Create New Order First"))
//                    } else if(event.productId.isEmpty()){
//                        _eventFlow.emit(UiEvent.OnError("Unable to get product"))
//                    }  else {
//                        when (val result = cartUseCases.addProductToCart(event.cartOrderId, event.productId)){
//                            is Resource.Loading -> {}
//                            is Resource.Success -> {
//                                _eventFlow.emit(UiEvent.OnSuccess("Item added to cart"))
//                            }
//                            is Resource.Error -> {
//                                _eventFlow.emit(UiEvent.OnError(result.message ?: "Error adding product to cart"))
//                                getSelectedCartOrder()
//                            }
//                        }
//                    }
//                }
//            }

            is MainFeedProductEvent.AddProductToCart -> {
                viewModelScope.launch {
                    when(val result = cartUseCases.addProductToCart(event.cartOrderId, event.productId)) {
                        is Resource.Loading -> _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        is Resource.Success -> _eventFlow.emit(UiEvent.OnSuccess(successMessage = "Item added to cart"))
                        is Resource.Error -> _eventFlow.emit(UiEvent.OnError(errorMessage = result.message ?: "Error adding product to cart"))
                    }
                }
            }

            is MainFeedProductEvent.RemoveProductFromCart -> {
                viewModelScope.launch {
                    when (val result = cartUseCases.removeProductFromCart(event.cartOrderId, event.productId)){
                        is Resource.Loading -> _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess(successMessage = "Item removed from cart"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(errorMessage = result.message ?: "Error removing product from cart"))
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: MainFeedEvent){
        when(event){
            is MainFeedEvent.RefreshMainFeed -> {
                _selectedCategory.value = ""
                onSearchBarCloseAndClearClick()
                getAllMainFeedProducts()
                getAllCategories()
                getSelectedCartOrder()
            }

            is MainFeedEvent.GetSelectedOrder -> {
                getSelectedCartOrder()
            }
        }
    }

    private fun getAllCategories(
        filterCategory: FilterCategory = FilterCategory.ByCategoryId(SortType.Ascending)
    ) {
        viewModelScope.launch {
            mainFeedUseCases.getMainFeedCategories(filterCategory).collect { result ->
                when(result){
                    is Resource.Loading -> {
                        _categories.value = _categories.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {categories ->
                            _categories.value = _categories.value.copy(
                                categories = categories,
                                filterCategory = filterCategory,
                            )
                        }
                    }
                    is Resource.Error -> {
                        _categories.value = _categories.value.copy(
                            error = result.message
                        )
                    }
                }

            }
        }
    }

    private fun getAllMainFeedProducts(
        filterProduct: FilterProduct = FilterProduct.ByProductId(SortType.Ascending),
        selectedCategory: String = "",
        searchText: String = "",
    ) {
        viewModelScope.launch {
            mainFeedUseCases.getMainFeedProducts(filterProduct, selectedCategory, searchText).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _products.value = _products.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let { products ->
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

    private fun getSelectedCartOrder(){
        viewModelScope.launch {
            mainFeedUseCases.getMainFeedSelectedOrder().collectLatest { result ->
                _selectedCartOrder.value = result
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
            getAllMainFeedProducts(
                _products.value.filterProduct,
                _selectedCategory.value,
                _searchText.value
            )
        }
    }
}