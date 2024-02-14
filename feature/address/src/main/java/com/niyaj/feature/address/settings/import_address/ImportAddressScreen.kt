package com.niyaj.feature.address.settings.import_address

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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.AddressTestTags.IMPORT_ADDRESS_NOTE_TEXT
import com.niyaj.common.tags.AddressTestTags.IMPORT_ADDRESS_TITLE
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.address.settings.AddressSettingViewModel
import com.niyaj.feature.address.settings.AddressSettingsEvent
import com.niyaj.feature.address.settings.components.ImportExportAddressContent
import com.niyaj.model.Address
import com.niyaj.ui.components.ImportExportHeader
import com.niyaj.ui.components.ImportFooter
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.niyaj.ui.util.ImportExport
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun ImportAddressScreen(
    navController: NavController,
    viewModel: AddressSettingViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyGridState()
    val context = LocalContext.current

    val isChosen = viewModel.onChoose

    val selectedAddresses = viewModel.selectedItems.toList()

    val importedData = viewModel.importedItems.collectAsStateWithLifecycle().value

    val showImportedBtn =
        if (isChosen) selectedAddresses.isNotEmpty() else importedData.isNotEmpty()

    var expanded by remember { mutableStateOf(false) }

    var importJob: Job? = null

    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    val data = ImportExport.readData<Address>(context, it)

                    viewModel.onEvent(AddressSettingsEvent.OnImportAddressItemsFromFile(data))
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
        text = IMPORT_ADDRESS_TITLE,
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
                    text = "Import " + if (isChosen) "${selectedAddresses.size} Selected Addresses" else " All Addresses",
                    onClickAll = {
                        viewModel.onChoose = false
                        viewModel.selectAllItems()
                    },
                    isChosen = isChosen,
                    onClickChoose = {
                        viewModel.onEvent(AddressSettingsEvent.OnChooseItems)
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
                    ImportExportAddressContent(
                        lazyListState = lazyListState,
                        addresses = importedData,
                        selectedAddresses = selectedAddresses,
                        expanded = expanded,
                        onExpandChanged = {
                            expanded = !expanded
                        },
                        onSelectAddress = viewModel::selectItem,
                        onClickSelectAll = viewModel::selectAllItems
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpaceMedium))

            ImportFooter(
                importButtonText = "Import ${if (isChosen) selectedAddresses.size else "All"} Address",
                noteText = IMPORT_ADDRESS_NOTE_TEXT,
                onClearImportedData = {
                    viewModel.onEvent(AddressSettingsEvent.ClearImportedAddresses)
                },
                onImportData = {
                    scope.launch {
                        viewModel.onEvent(AddressSettingsEvent.ImportAddressItemsToDatabase)
                    }
                },
                importedDataIsEmpty = importedData.isNotEmpty(),
                showImportedBtn = showImportedBtn,
                onOpenFile = {
                    scope.launch {
                        val result = ImportExport.openFile(context)
                        importLauncher.launch(result)
                    }
                },
            )
        }
    }
}