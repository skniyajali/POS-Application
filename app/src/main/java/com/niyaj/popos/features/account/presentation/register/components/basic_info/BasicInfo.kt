package com.niyaj.popos.features.account.presentation.register.components.basic_info

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.niyaj.popos.R
import com.niyaj.popos.common.utils.Constants.PRINT_LOGO
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.components.ImageCard
import com.niyaj.popos.features.components.NoteCard
import com.niyaj.popos.features.components.StandardOutlinedTextField

@Composable
fun BasicInfo(
    modifier : Modifier,
    lazyListState : LazyListState,
    tagline: String,
    taglineError: String? = null,
    address: String,
    addressError: String? = null,
    description: String,
    descriptionError : String? = null,
    paymentQRCode: String,
    paymentQRCodeError: String? = null,
    scannedBitmap: Bitmap? = null,
    printLogo: Bitmap? = null,
    defaultLogo: Int = PRINT_LOGO.toInt(),
    onChangeAddress: (BasicInfoEvent) -> Unit,
    onChangeDescription: (BasicInfoEvent) -> Unit,
    onChangePaymentQRCode: (BasicInfoEvent) -> Unit,
    onClickScanCode: (BasicInfoEvent) -> Unit,
    onChangeTagline: (BasicInfoEvent) -> Unit,
    onChangeLogo: () -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceMedium, Alignment.CenterVertically),
        state = lazyListState,
    ) {
        item("basic_title") {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceMini),
            ) {
                Text(
                    text = stringResource(R.string.basic_title),
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = stringResource(R.string.basic_subtitle),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Normal,
                    color = TextGray,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }

        item("print_logo") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                ImageCard(
                    defaultImage = defaultLogo,
                    bitmap = printLogo,
                    onEditClick = onChangeLogo,
                    size = DpSize(400.dp, 150.dp)
                )

                NoteCard(
                    text = "Please upload a logo with white background and 350x215 px size, for better print quality."
                )
            }
        }

        item("tagline_field") {
            StandardOutlinedTextField(
                text = tagline,
                label = "Restaurant Tagline",
                leadingIcon = Icons.AutoMirrored.Filled.StarHalf,
                error = taglineError,
                onValueChange = {
                    onChangeTagline(BasicInfoEvent.TaglineChanged(it))
                }
            )
        }

        item("description_field") {
            StandardOutlinedTextField(
                text = description,
                label = "Restaurant Description",
                singleLine = false,
                maxLines = 4,
                leadingIcon = Icons.AutoMirrored.Filled.Notes,
                error = descriptionError,
                onValueChange = {
                    onChangeDescription(BasicInfoEvent.DescriptionChanged(it))
                }
            )
        }

        item("address_field") {
            StandardOutlinedTextField(
                text = address,
                label = "Restaurant Address",
                singleLine = false,
                maxLines = 2,
                leadingIcon = Icons.Default.LocationOn,
                error = addressError,
                onValueChange = {
                    onChangeAddress(BasicInfoEvent.AddressChanged(it))
                }
            )
        }

        item("qrcode_field") {
            StandardOutlinedTextField(
                modifier = Modifier,
                text = paymentQRCode,
                label = "Restaurant Payment QR Code",
                leadingIcon = Icons.Default.QrCode,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onClickScanCode(BasicInfoEvent.StartScanning)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan QR Code"
                        )
                    }
                },
                error = paymentQRCodeError,
                singleLine = false,
                maxLines = 4,
                onValueChange = {
                    onChangePaymentQRCode(BasicInfoEvent.PaymentQRChanged(it))
                },
            )
        }

        if (scannedBitmap != null) {
            item("scannedBitmap") {
                Spacer(modifier = Modifier.height(SpaceSmall))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        bitmap = scannedBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}