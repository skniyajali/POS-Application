package com.niyaj.popos.features.addon_item.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.util.safeString
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.model.InvalidAddOnItemException
import com.niyaj.popos.features.addon_item.domain.use_cases.AddOnItemUseCases
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddEditAddOnItemViewModel @Inject constructor(
    private val addOnItemUseCases: AddOnItemUseCases,
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
                addEditState = addEditState.copy(itemPrice = safeString(event.itemPrice).toString())
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
        val validatedItemName = addOnItemUseCases.validateItemName(addEditState.itemName, addOnItemId)
        val validatedItemPrice = addOnItemUseCases.validateItemPrice(safeString(addEditState.itemPrice))

        val hasError = listOf(validatedItemName, validatedItemPrice).any {
            !it.successful
        }

        if (hasError) {
            addEditState = addEditState.copy(
                itemNameError = validatedItemName.errorMessage,
                itemPriceError = validatedItemPrice.errorMessage,
            )

            return
        }else {
            viewModelScope.launch {
                val addOnItem = AddOnItem()
                addOnItem.itemName = addEditState.itemName
                addOnItem.itemPrice = safeString(addEditState.itemPrice)


                if(addOnItemId.isNullOrEmpty()){
                    try {
                        when(val result = addOnItemUseCases.createNewAddOnItem(addOnItem)){
                            is Resource.Loading -> {
                                _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                            }
                            is Resource.Success -> {
                                _eventFlow.emit(UiEvent.OnSuccess(result.message ?: "AddOnItem created successfully"))
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to create AddOnItem"))
                            }
                        }
                    }catch (e: InvalidAddOnItemException) {
                        addEditState = addEditState.copy(
                            serverError = e.message
                        )
                    }
                }else {
                    val result = addOnItemUseCases.updateAddOnItem(
                        addOnItem,
                        addOnItemId
                    )
                    when(result){
                        is Resource.Error -> {
                            Timber.d(result.message)
                            _eventFlow.emit(UiEvent.OnError( "Unable to Update AddOnItems"))
                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("AddOnItem updated successfully"))
                        }
                    }
                }
            }

            addEditState = AddEditAddOnItemState()
        }
    }

    private fun getAllAddOnItemById(addOnItemId: String) {
        viewModelScope.launch {
            when(val result = addOnItemUseCases.getAddOnItemById(addOnItemId)) {
                is Resource.Loading -> {}

                is Resource.Success -> {
                    withContext(Dispatchers.Main){
                        result.data?.let {
                            addEditState = addEditState.copy(
                                itemName = result.data.itemName,
                                itemPrice = result.data.itemPrice.toString(),
                            )
                        }
                    }
                }
                is Resource.Error -> {
                }
            }
        }
    }

}