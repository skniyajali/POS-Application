package com.niyaj.feature.address.settings.export_address

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.AddressTestTags
import com.niyaj.common.tags.AddressTestTags.EXPORT_ADDRESS_FILE_NAME
import com.niyaj.common.tags.AddressTestTags.EXPORT_ADDRESS_TITLE
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.address.destinations.AddEditAddressScreenDestination
import com.niyaj.feature.address.settings.AddressSettingViewModel
import com.niyaj.feature.address.settings.AddressSettingsEvent
import com.niyaj.feature.address.settings.components.ImportExportAddressContent
import com.niyaj.ui.components.ExportedFooter
import com.niyaj.ui.components.ImportExportHeader
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.niyaj.ui.util.ImportExport
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun ExportAddressScreen(
    navController: NavController,
    viewModel: AddressSettingViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lazyListState = rememberLazyGridState()

    val hasStoragePermission =
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )

    val askForPermissions = {
        if (!hasStoragePermission.allPermissionsGranted) {
            hasStoragePermission.launchMultiplePermissionRequest()
        }
    }

    val isChosen = viewModel.onChoose

    val addressState = viewModel.addresses.collectAsStateWithLifecycle().value

    val selectedAddresses = viewModel.selectedItems.toList()

    val exportedData = viewModel.exportedItems.collectAsStateWithLifecycle().value

    var expanded by remember { mutableStateOf(false) }

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.let {
                scope.launch {
                    val result = ImportExport.writeData(context, it, exportedData)

                    if (result) {
                        resultBackNavigator.navigateBack("${exportedData.size} Addresses has been exported")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export addresses")
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
        text = EXPORT_ADDRESS_TITLE,
        icon = Icons.Default.Upload,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        if (addressState.isEmpty()) {
            ItemNotAvailable(
                text = AddressTestTags.ADDRESS_NOT_AVAILABLE,
                buttonText = AddressTestTags.CREATE_NEW_ADDRESS,
                onClick = {
                    navController.navigate(AddEditAddressScreenDestination())
                }
            )
        } else {
            val showFileSelector = if (isChosen) selectedAddresses.isNotEmpty() else true

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall)
            ) {
                ImportExportHeader(
                    modifier = Modifier,
                    text = "Export " + if (isChosen) "${selectedAddresses.size} Selected Addresses" else " All Addresses",
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
                        addresses = addressState,
                        selectedAddresses = selectedAddresses,
                        expanded = expanded,
                        onExpandChanged = {
                            expanded = !expanded
                        },
                        onSelectAddress = viewModel::selectItem,
                        onClickSelectAll = viewModel::selectAllItems
                    )
                }

                ExportedFooter(
                    text = "Export Addresses",
                    showFileSelector = showFileSelector,
                    onExportClick = {
                        scope.launch {
                            askForPermissions()
                            val result = ImportExport.createFile(
                                context = context,
                                fileName = EXPORT_ADDRESS_FILE_NAME
                            )
                            exportLauncher.launch(result)
                            viewModel.onEvent(AddressSettingsEvent.GetExportedItems)
                        }
                    }
                )
            }
        }
    }
}