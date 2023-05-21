package com.niyaj.popos.features.category.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.category.domain.repository.CategoryRepository
import com.niyaj.popos.features.category.domain.repository.CategoryValidationRepository
import com.niyaj.popos.features.category.domain.use_cases.GetAllCategories
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
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
class CategoryViewModel @Inject constructor(
    private val categoryUseCases: CategoryRepository,
    private val getAllCategories : GetAllCategories,
    private val validationRepository : CategoryValidationRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var addEditCategoryState by mutableStateOf(AddEditCategoryState())

    private val _categories = MutableStateFlow(CategoryState())
    val categories = _categories.asStateFlow()

    private val _selectedCategories = mutableStateListOf<String>()
    val selectedCategories: SnapshotStateList<String> = _selectedCategories

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    private var count: Int = 0

    var expanded by mutableStateOf(false)

    init {
        getAllCategory()

        savedStateHandle.get<String>("categoryId")?.let { categoryId ->
            getCategoryById(categoryId)
        }
    }

    /**
     *
     */
    fun onEvent(event: CategoryEvent) {
        when (event) {

            is CategoryEvent.CategoryNameChanged -> {
                addEditCategoryState = addEditCategoryState.copy(categoryName = event.categoryName.capitalizeWords)
            }

            is CategoryEvent.CategoryAvailabilityChanged -> {
                addEditCategoryState = addEditCategoryState.copy(
                    categoryAvailability = !addEditCategoryState.categoryAvailability
                )
            }

            is CategoryEvent.CreateNewCategory -> {
                createOrUpdateCategory()
            }

            is CategoryEvent.UpdateCategory -> {
                createOrUpdateCategory(event.categoryId)
            }

            is CategoryEvent.SelectCategory -> {
                viewModelScope.launch {
                    if(_selectedCategories.contains(event.categoryId)){
                        _selectedCategories.remove(event.categoryId)
                    }else{
                        _selectedCategories.add(event.categoryId)
                    }
                }
            }

            is CategoryEvent.SelectAllCategories -> {
                count += 1

                val categories = categories.value.categories

                if (categories.isNotEmpty()){
                    categories.forEach { category ->
                        if (count % 2 != 0){
                            val selectedCategory = _selectedCategories.find { it == category.categoryId }

                            if (selectedCategory == null){
                                _selectedCategories.add(category.categoryId)
                            }
                        }else {
                            _selectedCategories.remove(category.categoryId)
                        }
                    }
                }
            }

            is CategoryEvent.DeselectCategories -> {
                _selectedCategories.clear()
            }

            is CategoryEvent.DeleteCategories -> {
                viewModelScope.launch {
                    if (event.categories.isNotEmpty()){
                        event.categories.forEach { category ->
                            val result = categoryUseCases.deleteCategory(category)
                            _selectedCategories.removeIf { it == category }

                            when(result) {
                                is Resource.Loading -> {}
                                is Resource.Success -> {
                                    _eventFlow.emit(UiEvent.OnSuccess("Category Deleted Successfully"))
                                }
                                is Resource.Error -> {
                                    _eventFlow.emit(UiEvent.OnError("Unable to delete category"))
                                }
                            }
                        }
                    }
                }
            }

            is CategoryEvent.OnSearchCategory -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)
                    getAllCategory(event.searchText)
                }
            }

            is CategoryEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is CategoryEvent.RefreshCategory -> {
                getAllCategory()
            }
        }
    }

    private fun getCategoryById(categoryId: String) {
        viewModelScope.launch {
            when (val result = categoryUseCases.getCategoryById(categoryId)){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    result.data?.let {category ->
                        addEditCategoryState = addEditCategoryState.copy(
                            categoryName = category.categoryName,
                            categoryAvailability = category.categoryAvailability
                        )
                    }
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to get category"))
                }
            }
        }
    }

    /**
     *
     */
    fun onSearchBarCloseAndClearClick(){
        viewModelScope.launch {
            onSearchTextClearClick()

            _toggledSearchBar.emit(false)

        }
    }

    /**
     *
     */
    fun onSearchTextClearClick(){
        viewModelScope.launch {
            _searchText.emit("")
            getAllCategory(_searchText.value)
        }
    }

    private fun createOrUpdateCategory(categoryId: String? = null){
        val categoryNameResult = validationRepository.validateCategoryName(addEditCategoryState.categoryName, categoryId)

        if (!categoryNameResult.successful){
            addEditCategoryState = addEditCategoryState.copy(categoryNameError = categoryNameResult.errorMessage)
            return
        }else{
            viewModelScope.launch {
                val category = Category()
                category.categoryName = addEditCategoryState.categoryName
                category.categoryAvailability = addEditCategoryState.categoryAvailability

                if(categoryId.isNullOrEmpty()){

                    when(val result = categoryUseCases.createNewCategory(category)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Category Created Successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to create new order"))
                        }
                    }
                }else{
                    val result = categoryUseCases.updateCategory(
                        category,
                        categoryId
                    )

                    when(result){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(
                                UiEvent.OnSuccess(
                                successMessage = "Category Updated Successfully"
                            ))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError( result.message ?: "Unable to update new order"))
                        }
                    }
                }

                addEditCategoryState = AddEditCategoryState()

                onEvent(CategoryEvent.DeselectCategories)
            }
        }
    }

    private fun getAllCategory(searchText:String = ""){
        viewModelScope.launch {
            getAllCategories.invoke(searchText).collect { result ->
                when(result){
                    is Resource.Loading -> {
                        _categories.value = _categories.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _categories.value = _categories.value.copy(
                                categories = it
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
}