package com.niyaj.popos.features.product.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.category.domain.use_cases.GetAllCategories
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.common.util.safeString
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.product.domain.repository.ProductRepository
import com.niyaj.popos.features.product.domain.repository.ProductValidationRepository
import com.niyaj.popos.utils.capitalizeWords
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val getAllCategories : GetAllCategories,
    private val validationRepository : ProductValidationRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var addEditProductState by mutableStateOf(AddEditProductState())

    private val _categories = MutableStateFlow<List<Category>>(listOf())
    val categories = _categories.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var expanded by mutableStateOf(false)

    init {
        savedStateHandle.get<String>("productId")?.let { productId ->
            getProductById(productId)
        }

        getAllCategories()
    }

    /**
     * 
     */
    fun onAddEditEvent(event: AddEditProductEvent) {
        when(event){
            is AddEditProductEvent.CategoryNameChanged -> {
                addEditProductState = addEditProductState.copy(
                    category = event.category,
                )
            }

            is AddEditProductEvent.ProductNameChanged -> {
                addEditProductState = addEditProductState.copy(
                    productName = event.productName,
                )
            }

            is AddEditProductEvent.ProductPriceChanged -> {
                addEditProductState = addEditProductState.copy(
                    productPrice = event.productPrice,
                )
            }

            is AddEditProductEvent.ProductAvailabilityChanged -> {
                addEditProductState = addEditProductState.copy(
                    productAvailability = !addEditProductState.productAvailability
                )
            }

            is AddEditProductEvent.CreateNewProduct -> {
                createNewProduct()
            }

            is AddEditProductEvent.UpdateProduct -> {
                createNewProduct(event.productId)
            }
        }
    }

    private fun createNewProduct(productId: String? = null){
        val validatedProductName = validationRepository.validateProductName(addEditProductState.productName, productId)
        val validatedProductPrice = validationRepository.validateProductPrice(safeString(addEditProductState.productPrice))
        val validatedCategoryName = validationRepository.validateCategoryName(addEditProductState.category.categoryName)

        val hasError = listOf(validatedProductName, validatedProductPrice, validatedCategoryName).any {
            !it.successful
        }

        if(hasError) {
            addEditProductState = addEditProductState.copy(
                categoryError = validatedCategoryName.errorMessage,
                productPriceError = validatedProductPrice.errorMessage,
                productNameError = validatedProductName.errorMessage,
            )
            return
        }else{
            viewModelScope.launch {
                if(productId == null){
                    val result = productRepository.createNewProduct(
                        Product(
                            productName = addEditProductState.productName.capitalizeWords,
                            productPrice = safeString(addEditProductState.productPrice),
                            category = addEditProductState.category,
                            productAvailability = addEditProductState.productAvailability,
                        )
                    )

                    when(result){
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success(successMessage = "Product created successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to create new product"))
                        }
                    }
                }else{
                    val result = productRepository.updateProduct(
                        Product(
                            productName = addEditProductState.productName.capitalizeWords,
                            productPrice = safeString(addEditProductState.productPrice),
                            category = addEditProductState.category,
                            productAvailability = addEditProductState.productAvailability,
                        ),
                        productId
                    )

                    when(result){
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Product updated successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(errorMessage = result.message ?: "Unable to update product"))
                        }
                    }
                }

                addEditProductState = AddEditProductState()
            }
        }
    }

    private fun getProductById(productId: String) {
        viewModelScope.launch {
            when(val result = productRepository.getProductById(productId)){
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    result.data?.let {product ->
                        if (product.category != null) {
                            addEditProductState = addEditProductState.copy(
                                productName = product.productName,
                                category = product.category!!,
                                productPrice = product.productPrice.toString(),
                                productAvailability = product.productAvailability
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to find product"))
                }
            }
        }
    }

    private fun getAllCategories(){
        viewModelScope.launch {
            getAllCategories.invoke().collect { result ->
                when (result){
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _categories.emit(it)
                        }
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to load category"))
                    }
                }
            }
        }
    }

}