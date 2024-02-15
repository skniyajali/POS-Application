package com.niyaj.feature.customer.settings.import_customer

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CONTACT_NOTE_TEXT
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_NOTE_TEXT
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_TITLE
import com.niyaj.data.mapper.toCustomers
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.customer.components.ImportExportCustomerBody
import com.niyaj.feature.customer.settings.CustomerSettingsEvent
import com.niyaj.feature.customer.settings.CustomerSettingsViewModel
import com.niyaj.model.Customer
import com.niyaj.model.ImportContact
import com.niyaj.ui.components.ImportExportHeader
import com.niyaj.ui.components.ImportFooter
import com.niyaj.ui.components.MultiSelector
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.niyaj.ui.util.ImportExport
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Destination(style = DestinationStyleBottomSheet::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ImportContactScreen(
    navController: NavController = rememberNavController(),
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: CustomerSettingsViewModel = hiltViewModel(),
) {

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current

    val isChosen = viewModel.onChoose

    val selectedCustomers = viewModel.selectedCustomers.toList()

    val importedData = viewModel.importExportedCustomers.collectAsStateWithLifecycle().value

    val showImportedBtn =
        if (isChosen) selectedCustomers.isNotEmpty() else importedData.isNotEmpty()

    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedImportType by remember {
        mutableStateOf(ImportContactType.Customer.name)
    }

    var importJob: Job? = null

    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    if (selectedImportType == ImportContactType.Contact.name) {
                        val data = ImportExport.readData<ImportContact>(context, it)

                        viewModel.onEvent(CustomerSettingsEvent.ImportCustomerData(data.toCustomers()))
                    } else {
                        val newData = ImportExport.readData<Customer>(context, it)

                        viewModel.onEvent(CustomerSettingsEvent.ImportCustomerData(newData))
                    }
                }
            }
        }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = IMPORT_CUSTOMER_TITLE,
        icon = Icons.Default.SaveAlt,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall)
        ) {
            if (importedData.isNotEmpty()) {
                ImportExportHeader(
                    modifier = Modifier,
                    text = "Import " + if (isChosen) "${selectedCustomers.size} Selected Customers" else " All Customers",
                    onClickAll = {
                        viewModel.onChoose = false
                        viewModel.onEvent(CustomerSettingsEvent.SelectAllCustomer())
                    },
                    isChosen = isChosen,
                    onClickChoose = {
                        viewModel.onEvent(CustomerSettingsEvent.OnChooseCustomer)
                    }
                )

                AnimatedVisibility(
                    visible = isChosen,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (expanded) Modifier.weight(1.1F) else Modifier),
                ) {
                    ImportExportCustomerBody(
                        lazyListState = lazyListState,
                        customers = importedData,
                        selectedCustomers = selectedCustomers,
                        expanded = expanded,
                        onExpandChanged = {
                            expanded = !expanded
                        },
                        onSelectCustomer = {
                            viewModel.onEvent(CustomerSettingsEvent.SelectCustomer(it))
                        },
                        onClickSelectAll = {
                            viewModel.onEvent(CustomerSettingsEvent.SelectAllCustomer())
                        }
                    )
                }
            } else {
                val orderTypes = listOf(
                    ImportContactType.Contact.name, ImportContactType.Customer.name
                )
                val icons = listOf(
                    ImportContactType.Contact.icon, ImportContactType.Customer.icon
                )

                MultiSelector(
                    options = orderTypes,
                    icons = icons,
                    selectedOption = selectedImportType,
                    onOptionSelect = { option ->
                        selectedImportType = option
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            ImportFooter(
                importButtonText = "Import ${if (isChosen) selectedCustomers.size else "All"} Customer",
                noteText = if (selectedImportType == ImportContactType.Contact.name) {
                    IMPORT_CONTACT_NOTE_TEXT
                } else {
                    IMPORT_CUSTOMER_NOTE_TEXT
                },
                importedDataIsEmpty = importedData.isNotEmpty(),
                showImportedBtn = showImportedBtn,
                onClearImportedData = {
                    viewModel.onEvent(CustomerSettingsEvent.ClearImportedCustomer)
                },
                onImportData = {
                    viewModel.onEvent(CustomerSettingsEvent.ImportCustomers)
                },
                onOpenFile = {
                    scope.launch {
                        val result = ImportExport.openFile(context)
                        importLauncher.launch(result)
                    }
                }
            )
        }
    }
}


enum class ImportContactType(val icon: ImageVector) {
    Contact(Icons.Default.Contacts),
    Customer(Icons.Default.Person)
}
