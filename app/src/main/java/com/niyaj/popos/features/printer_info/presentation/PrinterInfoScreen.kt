package com.niyaj.popos.features.printer_info.presentation

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.InsertLink
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Print
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.popos.R
import com.niyaj.popos.common.utils.toPrettyDate
import com.niyaj.popos.common.utils.toSafeString
import com.niyaj.popos.features.common.ui.theme.Cream
import com.niyaj.popos.features.common.ui.theme.HintGray
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeMedium
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeSmall
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.NoteCard
import com.niyaj.popos.features.components.StandardChip
import com.niyaj.popos.features.components.StandardOutlinedChip
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.destinations.UpdatePrinterInfoDestination
import com.niyaj.popos.features.order.presentation.components.TwoGridText
import com.niyaj.popos.features.printer_info.domain.model.BluetoothDeviceState
import com.niyaj.popos.features.printer_info.domain.model.Printer
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Destination
fun PrinterInfoScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: PrinterInfoViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<UpdatePrinterInfoDestination, String>
) {
    val context = LocalContext.current

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
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            Timber.d("bluetoothLauncher", "Success")
        } else {
            Timber.w("bluetoothLauncher", "Failed")
        }
    }

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

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

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
        title = {
            Text(text = "Printer Information")
        },
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
        }
    ) { paddingValues ->
        Crossfade(targetState = uiState, label = "Printer Info State") { state ->
            when (state) {
                is PrinterInfoState.Loading -> LoadingIndicator()

                is PrinterInfoState.Empty -> {
                    ItemNotAvailable(
                        text = "Printer Information Not Available",
                        buttonText = "Update Printer Info",
                        icon = Icons.Default.Edit,
                        onClick = {
                            navController.navigate(UpdatePrinterInfoDestination())
                        }
                    )
                }

                is PrinterInfoState.Success -> {
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
                            PrinterInfo(info = state.info)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrinterInfoNotes() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall)
    ) {
        NoteCard(text = stringResource(R.string.printer_note_one))

        NoteCard(text = stringResource(R.string.printer_note_two))

        NoteCard(text = stringResource(R.string.printer_note_three))

        NoteCard(text = stringResource(R.string.printer_note_four))
    }
}

@Composable
fun BluetoothDevices(
    printers: List<BluetoothDeviceState>,
    onClickTestPrint: () -> Unit,
    onClickConnectPrinter: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
    ) {
        Text(
            text = "Printers",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        if (printers.isNotEmpty()) {
            printers.forEach { data ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(SpaceMini)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(
                                SpaceMini
                            )
                        ) {
                            TextWithIcon(
                                text = data.name,
                                icon = Icons.Default.Notes
                            )

                            TextWithIcon(
                                text = data.address,
                                icon = Icons.Default.InsertLink
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                SpaceSmall
                            )
                        ) {
                            StandardOutlinedChip(
                                text = "Test Print",
                                onClick = onClickTestPrint,
                            )

                            StandardChip(
                                text = if (data.connected) "Connected" else "Connect",
                                icon = if (!data.connected) Icons.Default.BluetoothConnected else Icons.Default.BluetoothDisabled,
                                isPrimary = data.connected,
                                isClickable = !data.connected,
                                onClick = {
                                    onClickConnectPrinter(data.address)
                                }
                            )
                        }

                    }
                }
            }
        } else {
            Text(
                text = "Bluetooth printer is not available on this device",
                style = MaterialTheme.typography.body1,
                color = HintGray,
            )
        }
    }
}

@Composable
fun PrinterInfo(
    info: Printer,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SpaceSmall),
        shape = RoundedCornerShape(SpaceSmall),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(ProfilePictureSizeMedium)
                    .background(Cream, CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = "Printer Info",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .size(ProfilePictureSizeSmall)
                        .align(Alignment.Center)
                )
            }

            Text(
                text = "Printer Information",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(SpaceSmall))

            Divider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Printer DPI",
                textTwo = info.printerDpi.toString()
            )

            Divider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Printer Width",
                textTwo = "${info.printerWidth} mm"
            )

            Divider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Printer NBR Lines",
                textTwo = info.printerNbrLines.toString()
            )

            Divider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Product Name Length",
                textTwo = info.productNameLength.toString()
            )

            Divider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Product Report Limit",
                textTwo = info.productWiseReportLimit.toString()
            )

            Divider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Address Report Limit",
                textTwo = info.addressWiseReportLimit.toString()
            )

            Divider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Customer Report Limit",
                textTwo = info.customerWiseReportLimit.toString()
            )

            Divider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Print QR Code",
                textTwo = info.printQRCode.toSafeString
            )

            Divider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Print Restaurant Logo",
                textTwo = info.printResLogo.toSafeString
            )

            Divider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Print Welcome Text",
                textTwo = info.printWelcomeText.toSafeString
            )

            Divider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Last Updated",
                textTwo = (info.updatedAt ?: info.createdAt).toPrettyDate()
            )
        }
    }
}