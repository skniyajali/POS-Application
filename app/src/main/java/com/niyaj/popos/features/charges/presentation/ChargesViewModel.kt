package com.niyaj.popos.features.charges.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.util.safeString
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.domain.use_cases.ChargesUseCases
import com.niyaj.popos.features.charges.domain.use_cases.validation.ValidateChargesName
import com.niyaj.popos.features.charges.domain.use_cases.validation.ValidateChargesPrice
import com.niyaj.popos.features.charges.domain.util.FilterCharges
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.common.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChargesViewModel @Inject constructor(
    private val validateChargesName: ValidateChargesName,
    private val validateChargesPrice: ValidateChargesPrice,
    private val chargesUseCases: ChargesUseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var state by mutableStateOf(ChargesState())

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
        getAllCharges(FilterCharges.ByChargesId(SortType.Descending))

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
                    when (val result = chargesUseCases.deleteCharges(event.chargesId)) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Charges deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete charges item"))
                        }
                    }
                }
                _selectedCharges.value = ""
            }


            is ChargesEvent.OnFilterCharges -> {
                if(state.filterCharges::class == event.filterCharges::class &&
                    state.filterCharges.sortType == event.filterCharges.sortType
                ){
                    state = state.copy(
                        filterCharges = FilterCharges.ByChargesId(SortType.Descending)
                    )
                    return
                }

                state = state.copy(
                    filterCharges = event.filterCharges
                )

                getAllCharges(event.filterCharges)
            }

            is ChargesEvent.OnSearchCharges -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)

                    delay(500L)
                    getAllCharges(
                        state.filterCharges,
                        searchText = event.searchText
                    )
                }
            }

            is ChargesEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is ChargesEvent.RefreshCharges -> {
                getAllCharges(state.filterCharges)
            }
        }
    }

    private fun getAllCharges(filterCharges: FilterCharges, searchText: String = "") {
        viewModelScope.launch {
            chargesUseCases.getAllCharges(filterCharges, searchText).collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        state = state.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            state = state.copy(
                                chargesItem = it,
                                filterCharges = filterCharges
                            )
                        }
                    }
                    is Resource.Error -> {
                        state = state.copy(error = "Unable to load resources")
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to load resources"))
                    }
                }
            }
        }
    }

    private fun addOrEditCharges(chargesId: String? = null){
        val validatedChargesName = validateChargesName.execute(addEditState.chargesName, chargesId)
        val validatedChargesPrice = validateChargesPrice.execute(addEditState.chargesApplicable, safeString(addEditState.chargesPrice))

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
                val charges = Charges()
                charges.chargesName = addEditState.chargesName
                charges.chargesPrice = safeString(addEditState.chargesPrice)
                charges.isApplicable = addEditState.chargesApplicable

                if(chargesId.isNullOrEmpty()){
                    when(val result = chargesUseCases.createNewCharges(charges)){
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess(result.message ?: "Charges created successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to create Charges Item"))
                        }
                    }

                }else {

                    val result = chargesUseCases.updateCharges(
                        charges,
                        chargesId
                    )
                    when(result){
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError( "Unable to Update Charges Item"))
                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Charges Item updated successfully"))
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
            when(val result = chargesUseCases.getChargesById(chargesId)) {
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
            getAllCharges(
                state.filterCharges,
                _searchText.value
            )
        }
    }
}