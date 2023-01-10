package com.niyaj.popos.features.app_settings.presentation.print_setting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.LightColor14
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardScaffold
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalLifecycleComposeApi::class)
@RequiresApi(Build.VERSION_CODES.R)
@Destination
@Composable
fun PrintSettingsScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    printSettingViewModel: PrintSettingViewModel = hiltViewModel(),
) {
    val bluetoothDevices = printSettingViewModel.bluetoothDevices.collectAsStateWithLifecycle().value

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