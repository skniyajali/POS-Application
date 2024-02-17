package com.niyaj.feature.addonitem.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.data.repository.AddOnItemRepository
import com.niyaj.data.repository.validation.AddOnItemValidationRepository
import com.niyaj.model.AddOnItem
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.safeInt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAddOnItemViewModel @Inject constructor(
    private val repository: AddOnItemRepository,
    private val validationRepository: AddOnItemValidationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val addOnItemId = savedStateHandle.get<String>("addOnItemId") ?: ""

    var addEditState by mutableStateOf(AddEditAddOnItemState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("addOnItemId")?.let { addOnItemId ->
            getAllAddOnItemById(addOnItemId)
        }
    }

    val nameError: StateFlow<String?> = snapshotFlow { addEditState.itemName }
        .mapLatest {
            validationRepository.validateItemName(it, addOnItemId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val priceError: StateFlow<String?> = snapshotFlow { addEditState.itemPrice }
        .mapLatest {
            validationRepository.validateItemPrice(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onEvent(event: AddOnEvent) {
        when (event) {
            is AddOnEvent.ItemNameChanged -> {
                addEditState = addEditState.copy(itemName = event.itemName)
            }

            is AddOnEvent.ItemPriceChanged -> {
                addEditState = addEditState.copy(itemPrice = event.itemPrice.safeInt())
            }

            is AddOnEvent.ItemApplicableChanged -> {
                addEditState = addEditState.copy(isApplicable = !addEditState.isApplicable)
            }

            is AddOnEvent.CreateUpdateAddOnItem -> {
                createOrUpdateAddOnItem(addOnItemId)
            }
        }
    }

    private fun getAllAddOnItemById(itemId: String) {
        if (itemId.isNotEmpty()) {
            viewModelScope.launch {
                when (val result = repository.getAddOnItemById(itemId)) {
                    is Resource.Success -> {
                        result.data?.let { addOnItem ->
                            addEditState = addEditState.copy(
                                itemName = addOnItem.itemName,
                                itemPrice = addOnItem.itemPrice,
                                isApplicable = addOnItem.isApplicable
                            )
                        }
                    }

                    is Resource.Error -> {
                        _eventFlow.emit(
                            UiEvent.Error(result.message ?: "Unable to get addon item")
                        )
                    }
                }
            }
        }
    }

    private fun createOrUpdateAddOnItem(addOnItemId: String = "") {
        viewModelScope.launch {
            if (nameError.value == null && priceError.value == null) {
                val addOnItem = AddOnItem(
                    addOnItemId = addOnItemId,
                    itemName = addEditState.itemName.capitalizeWords.trimEnd(),
                    itemPrice = addEditState.itemPrice,
                    isApplicable = addEditState.isApplicable,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = if (addOnItemId.isNotEmpty()) System.currentTimeMillis()
                        .toString() else null
                )
                val message = if (addOnItemId.isEmpty()) "created" else "updated"

                when (val result = repository.createOrUpdateItem(addOnItem, addOnItemId)) {
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.Success("AddOnItem $message successfully"))
                    }

                    is Resource.Error -> {
                        _eventFlow.emit(
                            UiEvent.Error(
                                result.message ?: "Unable to $message AddOnItem"
                            )
                        )
                    }
                }

                addEditState = AddEditAddOnItemState()
            }
        }
    }

}