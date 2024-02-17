package com.niyaj.feature.charges.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.data.repository.ChargesRepository
import com.niyaj.data.repository.validation.ChargesValidationRepository
import com.niyaj.model.Charges
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.safeInt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditChargesViewModel @Inject constructor(
    private val chargesRepository: ChargesRepository,
    private val validationRepository: ChargesValidationRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val chargesId = savedStateHandle.get<String>("chargesId") ?: ""

    var addEditState by mutableStateOf(AddEditChargesState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("chargesId")?.let { chargesId ->
            getChargesById(chargesId)
        }
    }

    val nameError: StateFlow<String?> = snapshotFlow { addEditState.chargesName }
        .mapLatest {
            validationRepository.validateChargesName(it, chargesId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val priceError = snapshotFlow { addEditState.chargesPrice }.mapLatest { price ->
        validationRepository.validateChargesPrice(price.safeInt()).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun onEvent(event: AddEditChargesEvent) {
        when (event) {
            is AddEditChargesEvent.ChargesNameChanged -> {
                addEditState = addEditState.copy(chargesName = event.chargesName)
            }

            is AddEditChargesEvent.ChargesPriceChanged -> {
                addEditState = addEditState.copy(chargesPrice = event.chargesPrice)

            }

            is AddEditChargesEvent.ChargesApplicableChanged -> {
                addEditState =
                    addEditState.copy(chargesApplicable = !addEditState.chargesApplicable)
            }

            is AddEditChargesEvent.CreateOrUpdateCharges -> {
                createOrUpdateCharges()
            }
        }
    }

    private fun getChargesById(itemId: String) {
        if (itemId.isNotEmpty()) {
            viewModelScope.launch {
                when (val result = chargesRepository.getChargesById(itemId)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.Error("Unable to find charges"))
                    }

                    is Resource.Success -> {
                        result.data?.let { charges ->
                            addEditState = addEditState.copy(
                                chargesName = charges.chargesName,
                                chargesPrice = charges.chargesPrice.toString(),
                                chargesApplicable = charges.isApplicable
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createOrUpdateCharges() {
        viewModelScope.launch {
            if (nameError.value == null && priceError.value == null) {
                val newCharges = Charges(
                    chargesId = chargesId,
                    chargesName = addEditState.chargesName.trimEnd().capitalizeWords,
                    chargesPrice = addEditState.chargesPrice.safeInt(),
                    isApplicable = addEditState.chargesApplicable,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = if (chargesId.isNotEmpty()) System.currentTimeMillis()
                        .toString() else null
                )

                val message = if (chargesId.isEmpty()) "Created" else "Updated"

                when (chargesRepository.createOrUpdateCharges(newCharges, chargesId)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.Error("Unable To $message Charges."))

                    }

                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.Success("Charges $message Successfully."))
                    }
                }

                addEditState = AddEditChargesState()
            }
        }
    }
}