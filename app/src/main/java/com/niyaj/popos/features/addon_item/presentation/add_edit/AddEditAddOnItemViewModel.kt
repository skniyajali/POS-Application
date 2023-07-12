package com.niyaj.popos.features.addon_item.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemValidationRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.common.util.safeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAddOnItemViewModel @Inject constructor(
    private val validationRepository: AddOnItemValidationRepository,
    private val addOnItemRepository : AddOnItemRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var addEditState by mutableStateOf(AddEditAddOnItemState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("addOnItemId")?.let { addOnItemId ->
            getAllAddOnItemById(addOnItemId)
        }
    }

    fun onEvent(event: AddEditAddOnItemEvent){
        when(event){
            is AddEditAddOnItemEvent.ItemNameChanged -> {
                addEditState = addEditState.copy(itemName = event.itemName)
            }

            is AddEditAddOnItemEvent.ItemPriceChanged -> {
                addEditState = addEditState.copy(itemPrice = event.itemPrice)
            }

            is AddEditAddOnItemEvent.ItemApplicableChanged -> {
                addEditState = addEditState.copy(isApplicable = !addEditState.isApplicable)
            }

            is AddEditAddOnItemEvent.CreateNewAddOnItem -> {
                addOrEditAddOnItem()
            }

            is AddEditAddOnItemEvent.UpdateAddOnItem -> {
                addOrEditAddOnItem(event.addOnItemId)
            }
        }
    }

    private fun addOrEditAddOnItem(addOnItemId: String? = null){
        viewModelScope.launch {
            val validatedItemName = validationRepository.validateItemName(addEditState.itemName, addOnItemId)
            val validatedItemPrice = validationRepository.validateItemPrice(safeString(addEditState.itemPrice))

            val hasError = listOf(validatedItemName, validatedItemPrice).any {
                !it.successful
            }

            if (hasError) {
                addEditState = addEditState.copy(
                    itemNameError = validatedItemName.errorMessage,
                    itemPriceError = validatedItemPrice.errorMessage,
                )

                return@launch
            }else {
                val addOnItem = AddOnItem()
                addOnItem.itemName = addEditState.itemName
                addOnItem.itemPrice = safeString(addEditState.itemPrice)
                addOnItem.isApplicable = addEditState.isApplicable

                if(addOnItemId.isNullOrEmpty()){
                    when(val result = addOnItemRepository.createNewAddOnItem(addOnItem)){
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success(result.message ?: "AddOnItem created successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to create AddOnItem"))
                        }
                    }
                }else {
                    when(addOnItemRepository.updateAddOnItem(addOnItem, addOnItemId)){
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error( "Unable to Update AddOnItems"))
                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("AddOnItem updated successfully"))
                        }
                    }
                }

                addEditState = AddEditAddOnItemState()
            }
        }
    }

    private fun getAllAddOnItemById(addOnItemId: String) {
        viewModelScope.launch {
            when(val result = addOnItemRepository.getAddOnItemById(addOnItemId)) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    result.data?.let {
                        addEditState = addEditState.copy(
                            itemName = result.data.itemName,
                            itemPrice = result.data.itemPrice.toString(),
                            isApplicable = result.data.isApplicable
                        )
                    }
                }
                is Resource.Error -> {}
            }
        }
    }

}