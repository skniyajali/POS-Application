package com.niyaj.popos.features.customer.presentation.settings.export_customer

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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.ImportExport
import com.niyaj.popos.features.common.util.ImportExport.writeData
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ExportedFooter
import com.niyaj.popos.features.components.ImportExportHeader
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.customer.presentation.components.ImportExportCustomerBody
import com.niyaj.popos.features.customer.presentation.settings.CustomerSettingsEvent
import com.niyaj.popos.features.customer.presentation.settings.CustomerSettingsViewModel
import com.niyaj.popos.utils.Constants
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun ExportCustomerScreen(
    navController : NavController,
    viewModel : CustomerSettingsViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val context = LocalContext.current

    LaunchedEffect(key1 = true, key2 = Unit) {
        scope.launch {
            viewModel.onEvent(CustomerSettingsEvent.GetAllCustomer)
        }
    }

    val hasStoragePermission =
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        )

    val isChoose = viewModel.onChoose

    val customerState = viewModel.customers.collectAsStateWithLifecycle().value

    val selectedCustomers = viewModel.selectedCustomers.toList()

    val exportedData = viewModel.importExportedCustomers.collectAsStateWithLifecycle().value

    var expanded by remember {
        mutableStateOf(false)
    }

    val askForPermissions = {
        if (!hasStoragePermission.allPermissionsGranted) {
            hasStoragePermission.launchMultiplePermissionRequest()
        }
    }


    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.let {
                scope.launch {
                    val result = writeData(context, it, exportedData)

                    if(result){
                        resultBackNavigator.navigateBack("${exportedData.size} Customers has been exported")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export customers")
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
        text = stringResource(id = R.string.export_customers),
        icon = Icons.Default.Upload,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Crossfade(
            targetState = customerState,
            label = "Export Contact Crossfade"
        ) { customerState ->
            when {
                customerState.isLoading -> LoadingIndicator()
                customerState.customers.isNotEmpty() -> {
                    val showFileSelector = if (isChoose) selectedCustomers.isNotEmpty() else true
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall)
                    ) {
                        ImportExportHeader(
                            text = "Export " + if (isChoose) "${selectedCustomers.size} Selected Customer" else " All Customer",
                            isChosen = isChoose,
                            onClickChoose = {
                                viewModel.onEvent(CustomerSettingsEvent.OnChooseCustomer)
                            },
                            onClickAll = {
                                viewModel.onChoose = false
                                viewModel.onEvent(CustomerSettingsEvent.DeselectCustomers)
                            },
                        )

                        AnimatedVisibility(
                            visible = isChoose,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (expanded) Modifier.weight(1.1F) else Modifier),
                        ) {
                            ImportExportCustomerBody(
                                lazyListState = lazyListState,
                                customers = customerState.customers,
                                selectedCustomers = selectedCustomers,
                                expanded = expanded,
                                onExpandChanged = {
                                    expanded = !expanded
                                },
                                onSelectCustomer = {
                                    viewModel.onEvent(CustomerSettingsEvent.SelectCustomer(it))
                                },
                                onClickSelectAll = {
                                    viewModel.onEvent(CustomerSettingsEvent.SelectAllCustomer(Constants.ImportExportType.EXPORT))
                                }
                            )
                        }

                        ExportedFooter(
                            text = "Export Customers",
                            showFileSelector = showFileSelector,
                            onExportClick = {
                                scope.launch {
                                    askForPermissions()
                                    val result = ImportExport.createFile(
                                        context = context,
                                        fileName = EXPORTED_CUSTOMER_FILE_NAME
                                    )
                                    exportLauncher.launch(result)
                                    viewModel.onEvent(CustomerSettingsEvent.GetExportedCustomer)
                                }
                            }
                        )
                    }
                }
                else -> ItemNotAvailable(text = customerState.error ?: "Customers not available")
            }
        }
    }
}


const val EXPORTED_CUSTOMER_FILE_NAME = "customers"