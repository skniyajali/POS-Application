package com.niyaj.popos.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun StandardAlertDialog(
    onDismissRequest: () -> Unit = {},
    confirmButton: @Composable () -> Unit = {},
    dismissButton: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    text: @Composable () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        title = title,
        text = text,
        shape = RoundedCornerShape(4.dp),
    )
}