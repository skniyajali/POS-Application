package com.niyaj.feature.product.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.data.repository.ProductRepository
import com.niyaj.data.repository.validation.ProductValidationRepository
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.safeInt
import com.niyaj.ui.util.safeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val validationRepository: ProductValidationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId = savedStateHandle.get<String>("productId") ?: ""

    var state by mutableStateOf(AddEditProductState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _selectedCategory = MutableStateFlow(Category())
    val selectedCategory = _selectedCategory.asStateFlow()

    val categories = snapshotFlow { productId }.flatMapLatest {
        productRepository.getAllCategories()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryError: StateFlow<String?> = _selectedCategory
        .mapLatest {
            validationRepository.validateCategoryName(it.categoryName).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val nameError: StateFlow<String?> = snapshotFlow { state.productName }
        .mapLatest {
            validationRepository.validateProductName(it, productId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val priceError: StateFlow<String?> = snapshotFlow { state.productPrice }
        .mapLatest {
            validationRepository.validateProductPrice(safeString(it)).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    init {
        savedStateHandle.get<String>("productId")?.let { productId ->
            getProductById(productId)
        }
    }

    fun onEvent(event: AddEditProductEvent) {
        when (event) {
            is AddEditProductEvent.CategoryChanged -> {
                viewModelScope.launch {
                    _selectedCategory.value = event.category
                }
            }

            is AddEditProductEvent.ProductNameChanged -> {
                viewModelScope.launch {
                    state = state.copy(
                        productName = event.productName
                    )
                }
            }

            is AddEditProductEvent.ProductPriceChanged -> {
                viewModelScope.launch {
                    state = state.copy(
                        productPrice = event.productPrice
                    )
                }
            }

            is AddEditProductEvent.ProductAvailabilityChanged -> {
                viewModelScope.launch {
                    state = state.copy(
                        productAvailability = !state.productAvailability
                    )
                }
            }

            is AddEditProductEvent.AddOrUpdateProduct -> {
                createOrUpdateProduct(event.productId)
            }
        }
    }

    private fun createOrUpdateProduct(productId: String = "") {
        viewModelScope.launch {
            val hasError = listOf(nameError, priceError, categoryError).all {
                it.value != null
            }

            if (!hasError) {
                val newProduct = Product(
                    productId = productId,
                    category = _selectedCategory.value,
                    productName = state.productName.trim().capitalizeWords,
                    productPrice = state.productPrice.safeInt(),
                    productAvailability = state.productAvailability,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = if (productId.isEmpty()) null else System.currentTimeMillis()
                        .toString()
                )
                val message = if (productId.isEmpty()) "created" else "updated"

                when (val result = productRepository.createOrUpdateProduct(newProduct, productId)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.Error(result.message ?: "Unable"))
                    }

                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.Success("Product $message successfully"))
                    }
                }

                state = AddEditProductState()
            }
        }
    }

    private fun getProductById(productId: String) {
        viewModelScope.launch {
            when (val result = productRepository.getProductById(productId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error("Unable to retrieve product"))
                }

                is Resource.Success -> {
                    result.data?.let { product ->
                        getCategoryById(product.category?.categoryId ?: "")

                        state = state.copy(
                            productName = product.productName,
                            productPrice = product.productPrice.toString(),
                            productAvailability = product.productAvailability
                        )
                    }
                }
            }
        }
    }

    private fun getCategoryById(categoryId: String) {
        viewModelScope.launch {
            productRepository.getCategoryById(categoryId)?.let { category ->
                _selectedCategory.value = category
            }
        }
    }

}