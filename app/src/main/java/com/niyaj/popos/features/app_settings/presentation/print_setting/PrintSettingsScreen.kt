package com.niyaj.popos.features.app_settings.presentation.print_setting

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.LightColor14
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.PrintSettingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import io.sentry.compose.SentryTraced
import timber.log.Timber

/**
 * Printer Settings Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param printSettingViewModel
 * @see PrintSettingViewModel
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.R)
@Destination
@Composable
fun PrintSettingsScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    printSettingViewModel: PrintSettingViewModel = hiltViewModel(),
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
                    Manifest.permission.BLUETOOTH_ADMIN,
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
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                Timber.d("Bluetooth is already turned on")
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val bluetoothDevices = printSettingViewModel.bluetoothDevices.collectAsStateWithLifecycle().value

    SentryTraced(tag = PrintSettingsScreenDestination.route) {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = {},
            showBackArrow = true,
            navActions = {},
            title = {
                Text(text = "Printer Connections")
            }
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
            ) {
                if (bluetoothDevices.isEmpty()){
                    ItemNotAvailable(
                        text = stringResource(id = R.string.no_bluetooth_devices_available)
                    )
                }else{
                    LazyColumn {
                        items(bluetoothDevices){ bluetoothDevice ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp),
                                backgroundColor = LightColor14,
                                elevation = 1.dp,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceSmall),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = bluetoothDevice.name ?: "",
                                            style = MaterialTheme.typography.body1,
                                            fontWeight = FontWeight.Bold,
                                        )

                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        Text(
                                            text = bluetoothDevice.address ?: "",
                                            style = MaterialTheme.typography.body2,
                                            fontStyle = FontStyle.Italic,
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            bluetoothDevice.address?.let {
                                                printSettingViewModel.connectBluetoothPrinter(it)
                                            }
                                        },
                                        enabled = !bluetoothDevice.connected
                                    ) {
                                        Text(text = if(bluetoothDevice.connected) "Disconnect" else "Connect")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(SpaceMedium))
                        }
                    }
                }
            }
        }
    }
}