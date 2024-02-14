package com.niyaj.feature.printer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.InsertLink
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.niyaj.designsystem.theme.HintGray
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.BluetoothDeviceState
import com.niyaj.ui.components.StandardChip
import com.niyaj.ui.components.StandardOutlinedChip
import com.niyaj.ui.components.TextWithIcon

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
                                icon = Icons.AutoMirrored.Filled.Notes
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