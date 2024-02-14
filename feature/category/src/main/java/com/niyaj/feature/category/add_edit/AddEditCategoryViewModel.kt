package com.niyaj.feature.category.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.data.repository.validation.CategoryValidationRepository
import com.niyaj.model.Category
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val validationRepository: CategoryValidationRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val categoryId = savedStateHandle.get<String>("categoryId") ?: ""

    var state by mutableStateOf(AddEditCategoryState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val categoryNameError = snapshotFlow { state.categoryName }.mapLatest {
        validationRepository.validateCategoryName(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    init {
        savedStateHandle.get<String>("categoryId")?.let {
            getCategoryById(it)
        }
    }

    fun onEvent(event: CategoryEvent) {
        when (event) {
            is CategoryEvent.CategoryAvailabilityChanged -> {
                state = state.copy(
                    categoryAvailability = !state.categoryAvailability
                )
            }

            is CategoryEvent.CategoryNameChanged -> {
                state = state.copy(
                    categoryName = event.categoryName
                )
            }

            is CategoryEvent.CreateOrUpdateCategory -> {
                addOrEditCategory()
            }

        }
    }

    private fun addOrEditCategory() {
        viewModelScope.launch {
            val newCategory = Category(
                categoryId = categoryId,
                categoryName = state.categoryName,
                categoryAvailability = state.categoryAvailability,
            )

            if (categoryId.isEmpty()) {
                when (val result = categoryRepository.createNewCategory(newCategory)) {
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.Success("Category Created Successfully"))
                    }

                    is Resource.Error -> {
                        _eventFlow.emit(
                            UiEvent.Error(
                                result.message ?: "Unable to create new order"
                            )
                        )
                    }
                }
            } else {
                when (val result = categoryRepository.updateCategory(newCategory, categoryId)) {
                    is Resource.Success -> {
                        _eventFlow.emit(
                            UiEvent.Success(successMessage = "Category Updated Successfully")
                        )
                    }

                    is Resource.Error -> {
                        _eventFlow.emit(
                            UiEvent.Error(result.message ?: "Unable")
                        )
                    }
                }
            }

            state = AddEditCategoryState()
        }
    }


    private fun getCategoryById(categoryId: String) {
        viewModelScope.launch {
            when (val result = categoryRepository.getCategoryById(categoryId)) {
                is Resource.Success -> {
                    result.data?.let { category ->
                        state = state.copy(
                            categoryName = category.categoryName,
                            categoryAvailability = category.categoryAvailability
                        )
                    }
                }

                is Resource.Error -> {}
            }
        }
    }

}