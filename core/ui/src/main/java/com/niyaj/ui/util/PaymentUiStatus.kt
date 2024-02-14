package com.niyaj.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.ui.graphics.vector.ImageVector
import com.niyaj.model.PaymentStatus

sealed class PaymentUiStatus(val status: String, val icon: ImageVector, val order: Int) {
    data object NotPaid :
        PaymentUiStatus(status = "Not Paid", icon = Icons.Default.Close, order = 1)

    data object Absent :
        PaymentUiStatus(status = "Absent", icon = Icons.Default.EventBusy, order = 2)

    data object Paid : PaymentUiStatus(status = "Paid", icon = Icons.Default.HowToReg, order = 3)
}

fun PaymentStatus.toUiStatus(): PaymentUiStatus = when (this) {
    PaymentStatus.NotPaid -> PaymentUiStatus.NotPaid
    PaymentStatus.Paid -> PaymentUiStatus.Paid
    PaymentStatus.Absent -> PaymentUiStatus.Absent
}