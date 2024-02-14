package com.niyaj.feature.printer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_NOT_AVAILABLE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_SCREEN_TITLE
import com.niyaj.common.tags.PrinterInfoTestTags.UPDATE_PRINTER_INFO
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.printer.components.BluetoothDevices
import com.niyaj.feature.printer.components.PrinterInfo
import com.niyaj.feature.printer.components.PrinterInfoNotes
import com.niyaj.feature.printer.destinations.UpdatePrinterInfoDestination
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.event.UiState
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@RootNavGraph(start = true)
@Destination(route = Screens.PRINTER_INFO_SCREEN)
fun PrinterInfoScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: PrinterInfoViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<UpdatePrinterInfoDestination, String>
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()

    val bluetoothPermissions =
        // Checks if the device has Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                )
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN
                )
            )
        }

    val enableBluetoothContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    val bluetoothManager = remember {
        context.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter: BluetoothAdapter? = remember {
        bluetoothManager.adapter
    }

    LaunchedEffect(key1 = Unit) {
        if (!bluetoothPermissions.allPermissionsGranted) {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }

        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == false) {
                enableBluetoothContract.launch(enableBluetoothIntent)
            }
        }
    }

    val uiState = viewModel.info.collectAsStateWithLifecycle().value
    val printers = viewModel.printers.collectAsStateWithLifecycle().value

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        result.value
                    )
                }
            }
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        navActions = {
            IconButton(
                onClick = {
                    navController.navigate(UpdatePrinterInfoDestination())
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Printer Information"
                )
            }
        },
        title = {
            Text(text = PRINTER_SCREEN_TITLE)
        }
    ) { paddingValues ->
        Crossfade(
            targetState = uiState,
            label = "Printer Info State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = PRINTER_NOT_AVAILABLE,
                        buttonText = UPDATE_PRINTER_INFO,
                        icon = Icons.Default.Edit,
                        onClick = {
                            navController.navigate(UpdatePrinterInfoDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingValues)
                            .padding(SpaceSmall),
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(SpaceSmall)
                    ) {
                        item("Notes") { PrinterInfoNotes() }

                        item("Printers") {
                            BluetoothDevices(
                                printers = printers,
                                onClickTestPrint = viewModel::testPrint,
                                onClickConnectPrinter = viewModel::connectPrinter
                            )
                        }

                        item("Printer Information") {
                            PrinterInfo(info = state.data)
                        }
                    }
                }
            }
        }
    }
}
