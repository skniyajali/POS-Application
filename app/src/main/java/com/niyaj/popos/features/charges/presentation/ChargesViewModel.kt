package com.niyaj.popos.features.charges.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.domain.repository.ChargesRepository
import com.niyaj.popos.features.charges.domain.repository.ChargesValidationRepository
import com.niyaj.popos.features.charges.domain.use_cases.GetAllCharges
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.common.util.safeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChargesViewModel @Inject constructor(
    private val chargesRepository: ChargesRepository,
    private val validationRepository : ChargesValidationRepository,
    private val getAllCharges : GetAllCharges,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _state = MutableStateFlow(ChargesState())
    val state = _state.asStateFlow()

    var addEditState by mutableStateOf(AddEditChargesState())

    private val _selectedCharges =  MutableStateFlow("")
    val selectedCharges = _selectedCharges.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    var expanded by mutableStateOf(false)

    init {
        getAllCharges()

        savedStateHandle.get<String>("chargesId")?.let { chargesId ->
            getChargesById(chargesId)
        }
    }

    fun onChargesEvent(event: ChargesEvent) {
        when (event){

            is ChargesEvent.ChargesNameChanged -> {
                addEditState = addEditState.copy(chargesName = event.chargesName)
            }

            is ChargesEvent.ChargesPriceChanged -> {
                addEditState = addEditState.copy(chargesPrice = safeString(event.chargesPrice).toString())
            }

            is ChargesEvent.ChargesApplicableChanged -> {
                addEditState = addEditState.copy(chargesApplicable = !addEditState.chargesApplicable)
            }

            is ChargesEvent.SelectCharges -> {
                viewModelScope.launch {
                    if(_selectedCharges.value.isNotEmpty() && _selectedCharges.value == event.chargesId){
                        _selectedCharges.emit("")
                    }else{
                        _selectedCharges.emit(event.chargesId)
                    }
                }
            }

            is ChargesEvent.CreateNewCharges -> {
                addOrEditCharges()
            }

            is ChargesEvent.UpdateCharges -> {
                addOrEditCharges(event.chargesId)
            }

            is ChargesEvent.DeleteCharges -> {
                viewModelScope.launch {
                    when (val result = chargesRepository.deleteCharges(event.chargesId)) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Charges deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to delete charges item"))
                        }
                    }
                }
                _selectedCharges.value = ""
            }

            is ChargesEvent.OnSearchCharges -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)
                    getAllCharges(searchText = event.searchText)
                }
            }

            is ChargesEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is ChargesEvent.RefreshCharges -> {
                getAllCharges()
            }
        }
    }

    private fun getAllCharges(searchText: String = "") {
        viewModelScope.launch {
            getAllCharges.invoke(searchText).collectLatest{ result ->
                when(result){
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _state.value = _state.value.copy(chargesItem = it)
                        }
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(error = result.message)
                    }
                }
            }
        }
    }

    private fun addOrEditCharges(chargesId: String? = null){
        val validatedChargesName = validationRepository.validateChargesName(addEditState.chargesName, chargesId)
        val validatedChargesPrice = validationRepository.validateChargesPrice(addEditState.chargesApplicable, safeString(addEditState.chargesPrice))

        val hasError = listOf(validatedChargesName, validatedChargesPrice).any {
            !it.successful
        }

        if (hasError) {
            addEditState = addEditState.copy(
                chargesNameError = validatedChargesName.errorMessage,
                chargesPriceError = validatedChargesPrice.errorMessage,
            )

            return
        }else {
            viewModelScope.launch {
                val charges = Charges(
                    chargesName = addEditState.chargesName,
                    chargesPrice = safeString(addEditState.chargesPrice),
                    isApplicable = addEditState.chargesApplicable
                )


                if(chargesId.isNullOrEmpty()){
                    when(val result = chargesRepository.createNewCharges(charges)){
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success(result.message ?: "Charges created successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to create Charges Item"))
                        }
                    }

                }else {
                    when(val result = chargesRepository.updateCharges(charges, chargesId)){
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error( result.message ?: "Unable to Update Charges Item"))
                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Charges Item updated successfully"))
                        }
                    }
                }
            }

            addEditState = AddEditChargesState()
            _selectedCharges.value = ""
        }
    }

    private fun getChargesById(chargesId: String) {
        viewModelScope.launch {
            when(val result = chargesRepository.getChargesById(chargesId)) {
                is Resource.Loading -> {}

                is Resource.Success -> {
                    result.data?.let {
                        addEditState = addEditState.copy(
                            chargesName = result.data.chargesName,
                            chargesPrice = result.data.chargesPrice.toString(),
                            chargesApplicable = result.data.isApplicable
                        )
                    }
                }
                is Resource.Error -> {}
            }
        }
    }

    fun onSearchBarCloseAndClearClick(){
        viewModelScope.launch {
            onSearchTextClearClick()

            _toggledSearchBar.emit(false)

        }
    }

    fun onSearchTextClearClick(){
        viewModelScope.launch {
            _searchText.emit("")
            getAllCharges(_searchText.value)
        }
    }
}