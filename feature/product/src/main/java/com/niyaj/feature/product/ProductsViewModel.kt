package com.niyaj.feature.product

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.data.repository.ProductRepository
import com.niyaj.feature.product.components.ViewType
import com.niyaj.model.Category
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : BaseViewModel() {

    override var totalItems: List<String> = emptyList()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory = _selectedCategory.asStateFlow()

    val products =
        snapshotFlow { searchText.value }.combine(selectedCategory) { searchText, category ->
            productRepository.getAllProducts(searchText, category)
        }.flatMapLatest { flow ->
            flow.map { list ->
                totalItems = list.map { it.productId }

                if (list.isEmpty()) UiState.Empty else UiState.Success(list)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _viewType = MutableStateFlow(ViewType.COLUMN)
    val viewType = _viewType.asStateFlow()

    private var productCount = 0

    init {
        getAllCategories()
    }

    fun onChangeViewType(viewType: ViewType) {
        viewModelScope.launch {
            _viewType.value = viewType
        }
    }

    fun selectCategory(categoryId: String) {
        viewModelScope.launch {
            if (_selectedCategory.value == categoryId) {
                _selectedCategory.value = ""
            } else {
                _selectedCategory.value = categoryId
            }
        }
    }

    fun selectProducts(products: List<String>) {
        productCount += 1

        if (products.isNotEmpty()){
            viewModelScope.launch {
                products.forEach { product ->
                    if(productCount % 2 != 0){
                        val selectedProduct = mSelectedItems.find { it == product }

                        if (selectedProduct == null){
                            mSelectedItems.add(product)
                        }
                    }else {
                        mSelectedItems.remove(product)
                    }
                }
            }
        }
    }

    private fun getAllCategories() {
        viewModelScope.launch {
            productRepository.getAllCategories().collectLatest { result ->
                _categories.value = result
            }
        }
    }
}