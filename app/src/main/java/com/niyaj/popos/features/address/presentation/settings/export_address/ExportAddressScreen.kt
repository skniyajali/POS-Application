package com.niyaj.popos.features.address.presentation.settings.export_address

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.popos.R
import com.niyaj.popos.features.address.presentation.settings.AddressSettingEvent
import com.niyaj.popos.features.address.presentation.settings.AddressSettingViewModel
import com.niyaj.popos.features.address.presentation.settings.components.ImportExportAddressContent
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.ImportExport
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ExportedFooter
import com.niyaj.popos.features.components.ImportExportHeader
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.utils.Constants.ImportExportType
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun ExportAddressScreen(
    navController : NavController,
    viewModel: AddressSettingViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyGridState()

    val context = LocalContext.current

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

    LaunchedEffect(key1 = true, key2 = Unit) {
        scope.launch {
            viewModel.onEvent(AddressSettingEvent.GetAllAddress)
        }
    }

    val isChosen = viewModel.onChoose

    val addressState = viewModel.exportState.collectAsStateWithLifecycle().value

    val selectedAddresses = viewModel.selectedAddresses.toList()

    val exportedData = viewModel.importExportAddresses.collectAsStateWithLifecycle().value

    var expanded by remember {
        mutableStateOf(false)
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.let {
                scope.launch {
                    val result = ImportExport.writeData(context, it, exportedData)

                    if(result){
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

                is UiEvent.IsLoading -> {}
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.export_addresses),
        icon = Icons.Default.Upload,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Crossfade(
            targetState = addressState,
            label = "Export Contact Crossfade"
        ) { state ->
            when {
                state.isLoading -> LoadingIndicator()
                state.addresses.isNotEmpty() -> {
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
                                viewModel.onEvent(AddressSettingEvent.DeselectAddresses)
                            },
                            isChosen = isChosen,
                            onClickChoose = {
                                viewModel.onEvent(AddressSettingEvent.OnChooseAddress)
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
                                addresses = state.addresses,
                                selectedAddresses = selectedAddresses,
                                expanded = expanded,
                                onExpandChanged = {
                                    expanded = !expanded
                                },
                                onSelectAddress = {
                                    viewModel.onEvent(AddressSettingEvent.SelectAddress(it))
                                },
                                onClickSelectAll = {
                                    viewModel.onEvent(
                                        AddressSettingEvent.SelectAllAddress(ImportExportType.EXPORT)
                                    )
                                }
                            )
                        }

                        ExportedFooter(
                            text = "Export Addresses",
                            showFileSelector = showFileSelector,
                            onExportClick = {
                                scope.launch {
                                    askForPermissions()
                                    val result = ImportExport.createFile(context = context, fileName = "address")
                                    exportLauncher.launch(result)
                                    viewModel.onEvent(AddressSettingEvent.GetExportedAddress)
                                }
                            }
                        )
                    }
                }
                else -> ItemNotAvailable(text = state.error ?: "Addresses not available")
            }
        }
    }
}