package com.niyaj.popos.features.customer.presentation.settings.import_contact

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.customer.domain.model.Contact
import com.niyaj.popos.features.customer.domain.use_cases.CustomerUseCases
import com.niyaj.popos.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportContactViewModel @Inject constructor(
    private val customerUseCases: CustomerUseCases
): ViewModel() {

    private val _importedContacts = mutableStateListOf<Contact>()
    val importedContacts : MutableList<Contact> = _importedContacts

    private val _selectedContacts = mutableStateListOf<String>()
    val selectedContacts: SnapshotStateList<String> = _selectedContacts

    private val _selectedFileType = mutableStateOf(Constants.JSON_FILE_NAME)
    val selectedFileType : State<String> = _selectedFileType

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var onChoose by mutableStateOf(false)

    private var count: Int = 1

    private var contactCount = 1

    fun onEvent(event: ImportContactEvent){
        when (event) {

            is ImportContactEvent.SelectContact -> {
                viewModelScope.launch {
                    if(_selectedContacts.contains(event.contactId)){
                        _selectedContacts.remove(event.contactId)
                    }else{
                        _selectedContacts.add(event.contactId)
                    }
                }

            }

            is ImportContactEvent.SelectContacts -> {
                contactCount += 1

                if (event.contacts.isNotEmpty()){
                    viewModelScope.launch {
                        event.contacts.forEach { contact ->
                            if(contactCount % 2 != 0){
                                val selectedContact = _selectedContacts.find { it == contact }

                                if (selectedContact == null){
                                    _selectedContacts.add(contact)
                                }
                            }else {
                                _selectedContacts.remove(contact)
                            }
                        }
                    }
                }
            }

            is ImportContactEvent.SelectAllContact -> {
                count += 1

                val products = _importedContacts.toList()

                if (products.isNotEmpty()){
                    products.forEach { contact ->
                        if (count % 2 != 0){

                            val selectedContact = _selectedContacts.find { it == contact.contactId }

                            if (selectedContact == null){
                                _selectedContacts.add(contact.contactId)
                            }
                        }else {
                            _selectedContacts.remove(contact.contactId)
                        }
                    }
                }
            }

            is ImportContactEvent.DeselectContacts -> {
                _selectedContacts.clear()
            }

            is ImportContactEvent.OnChooseContact -> {
                onChoose = !onChoose
            }

            is ImportContactEvent.ImportContactsData -> {
                _importedContacts.clear()

                if (event.contacts.isNotEmpty()) {
                    _importedContacts.addAll(event.contacts)
                    _selectedContacts.addAll(event.contacts.map { it.contactId })
                }
            }

            is ImportContactEvent.ImportContacts -> {
                val contacts = mutableStateListOf<Contact>()

                _selectedContacts.forEach {
                    val data = _importedContacts.find { contact -> contact.contactId == it }
                    if (data != null) contacts.add(data)
                }

                viewModelScope.launch {
                    when (val result = customerUseCases.importContacts(contacts.toList())){
                        is Resource.Loading -> { }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("${contacts.toList().size} customers imported successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to import customers"))
                        }
                    }
                }
            }

            is ImportContactEvent.ClearImportedContacts -> {
                _importedContacts.clear()
                _selectedContacts.clear()
                onChoose = false
            }
        }
    }

}