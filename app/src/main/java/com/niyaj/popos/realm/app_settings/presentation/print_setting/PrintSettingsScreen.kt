package com.niyaj.popos.realm.app_settings.presentation.print_setting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.popos.presentation.components.StandardScaffold
import com.niyaj.popos.presentation.ui.theme.LightColor14
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.ramcosta.composedestinations.annotation.Destination

@RequiresApi(Build.VERSION_CODES.R)
@Destination
@Composable
fun PrintSettingsScreen(
    navController: NavController,
    printSettingViewModel: PrintSettingViewModel = hiltViewModel(),
) {
    val scaffoldState = rememberScaffoldState()
    val bluetoothDevices = printSettingViewModel.bluetoothDevices.collectAsState().value


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

            Text(text = "Print Settings")

            Spacer(modifier = Modifier.height(SpaceMedium))

            LazyColumn(){
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