package com.niyaj.popos.features.main_feed.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.cart.domain.repository.CartRepository
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.category.domain.use_cases.GetAllCategories
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import com.niyaj.popos.features.main_feed.domain.use_cases.GetMainFeedProducts
import com.niyaj.popos.features.main_feed.presentation.components.category.MainFeedCategoryEvent
import com.niyaj.popos.features.main_feed.presentation.components.category.MainFeedCategoryState
import com.niyaj.popos.features.main_feed.presentation.components.product.MainFeedProductEvent
import com.niyaj.popos.features.main_feed.presentation.components.product.MainFeedProductState
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
    private val getMainFeedProducts: GetMainFeedProducts,
    private val getAllCategories : GetAllCategories,
    private val cartRepository: CartRepository,
    private val mainFeedRepository : MainFeedRepository,
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
    
    fun onCategoryEvent(event: MainFeedCategoryEvent){
        when(event){
            is MainFeedCategoryEvent.OnSelectCategory -> {
                if(event.categoryId == _selectedCategory.value){
                    _selectedCategory.value = ""
                    getAllMainFeedProducts(_selectedCategory.value, _searchText.value)
                }else{
                    getAllMainFeedProducts(event.categoryId, _searchText.value)
                    _selectedCategory.value = event.categoryId
                }
            }
        }
    }

    fun onProductEvent(event: MainFeedProductEvent){
        when(event){
            is MainFeedProductEvent.SearchProduct -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)
                    getAllMainFeedProducts(_selectedCategory.value, event.searchText)
                }
            }

            is MainFeedProductEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }
            
            is MainFeedProductEvent.AddProductToCart -> {
                viewModelScope.launch {
                    when(val result = cartRepository.addProductToCart(event.cartOrderId, event.productId)) {
                        is Resource.Loading -> _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        is Resource.Success -> _eventFlow.emit(UiEvent.OnSuccess(successMessage = "Item added to cart"))
                        is Resource.Error -> _eventFlow.emit(UiEvent.OnError(errorMessage = result.message ?: "Error adding product to cart"))
                    }
                }
            }

            is MainFeedProductEvent.RemoveProductFromCart -> {
                viewModelScope.launch {
                    when (val result = cartRepository.removeProductFromCart(event.cartOrderId, event.productId)){
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

    private fun getAllCategories() {
        viewModelScope.launch {
            getAllCategories.invoke().collect { result ->
                when(result){
                    is Resource.Loading -> {
                        _categories.value = _categories.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {categories ->
                            _categories.value = _categories.value.copy(categories = categories)
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

    private fun getAllMainFeedProducts(selectedCategory : String = "", searchText : String = "") {
        viewModelScope.launch {
            getMainFeedProducts.invoke(selectedCategory, searchText).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _products.value = _products.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let { products ->
                            _products.value =  _products.value.copy(products = products)
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
            mainFeedRepository.getSelectedCartOrders().collectLatest { result ->
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
            getAllMainFeedProducts(_selectedCategory.value, _searchText.value)
        }
    }
}