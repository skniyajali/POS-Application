package com.niyaj.feature.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.CartRepository
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.data.repository.HomeRepository
import com.niyaj.feature.home.components.category.MainFeedCategoryEvent
import com.niyaj.feature.home.components.category.MainFeedCategoryState
import com.niyaj.feature.home.components.product.MainFeedProductEvent
import com.niyaj.feature.home.components.product.MainFeedProductState
import com.niyaj.model.CartOrder
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val cartRepository: CartRepository,
    private val homeRepository: HomeRepository,
) : ViewModel() {

    private val _selectedCategory = mutableStateOf("")
    val selectedCategory: State<String> = _selectedCategory

    private val _categories = MutableStateFlow(MainFeedCategoryState())
    val categories = _categories.asStateFlow()

    private val _products = MutableStateFlow(MainFeedProductState())
    val products = _products.asStateFlow()

    private val _selectedCartOrder = MutableStateFlow<CartOrder?>(null)
    val selectedCartOrder = _selectedCartOrder.asStateFlow()

    private val _searchText = MutableStateFlow("")
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

    fun onCategoryEvent(event: MainFeedCategoryEvent) {
        when (event) {
            is MainFeedCategoryEvent.OnSelectCategory -> {
                if (event.categoryId == _selectedCategory.value) {
                    _selectedCategory.value = ""
                    getAllMainFeedProducts(_selectedCategory.value, _searchText.value)
                } else {
                    getAllMainFeedProducts(event.categoryId, _searchText.value)
                    _selectedCategory.value = event.categoryId
                }
            }
        }
    }

    fun onProductEvent(event: MainFeedProductEvent) {
        when (event) {
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
                    when (val result =
                        cartRepository.addProductToCart(event.cartOrderId, event.productId)) {
                        is Resource.Success -> _eventFlow.emit(UiEvent.Success(successMessage = "Item added to cart"))
                        is Resource.Error -> _eventFlow.emit(
                            UiEvent.Error(
                                errorMessage = result.message ?: "Error adding product to cart"
                            )
                        )
                    }
                }
            }

            is MainFeedProductEvent.RemoveProductFromCart -> {
                viewModelScope.launch {
                    when (val result =
                        cartRepository.removeProductFromCart(event.cartOrderId, event.productId)) {
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success(successMessage = "Item removed from cart"))
                        }

                        is Resource.Error -> {
                            _eventFlow.emit(
                                UiEvent.Error(
                                    errorMessage = result.message
                                        ?: "Error removing product from cart"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.RefreshHome -> {
                _selectedCategory.value = ""
                onSearchBarCloseAndClearClick()
                getAllMainFeedProducts()
                getAllCategories()
                getSelectedCartOrder()
            }

            is HomeEvent.GetSelectedOrder -> {
                getSelectedCartOrder()
            }
        }
    }

    private fun getAllCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories("").collect { result ->
                _categories.value = _categories.value.copy(
                    categories = result,
                    isLoading = false,
                )
            }
        }
    }

    private fun getAllMainFeedProducts(selectedCategory: String = "", searchText: String = "") {
        viewModelScope.launch {
            homeRepository.geMainFeedProducts(selectedCategory, searchText)
                .collectLatest { result ->
                    _products.value = _products.value.copy(
                        products = result,
                        isLoading = false,
                    )
                }
        }
    }

    private fun getSelectedCartOrder() {
        viewModelScope.launch {
            homeRepository.getSelectedCartOrders().collectLatest { result ->
                _selectedCartOrder.value = result
            }
        }
    }

    fun onSearchBarCloseAndClearClick() {
        viewModelScope.launch {
            onSearchTextClearClick()

            _toggledSearchBar.emit(false)

        }
    }

    fun onSearchTextClearClick() {
        viewModelScope.launch {
            _searchText.emit("")
            getAllMainFeedProducts(_selectedCategory.value, _searchText.value)
        }
    }
}