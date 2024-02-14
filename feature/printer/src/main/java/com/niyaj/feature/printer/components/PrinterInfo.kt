package com.niyaj.feature.printer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toSafeString
import com.niyaj.designsystem.theme.Cream
import com.niyaj.designsystem.theme.ProfilePictureSizeMedium
import com.niyaj.designsystem.theme.ProfilePictureSizeSmall
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Printer
import com.niyaj.ui.components.TwoGridText

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