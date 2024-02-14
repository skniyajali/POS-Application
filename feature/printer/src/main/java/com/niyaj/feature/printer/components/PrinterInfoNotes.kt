package com.niyaj.feature.printer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.common.tags.PrinterInfoTestTags
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.NoteCard

@Composable
fun PrinterInfoNotes() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall)
    ) {
        NoteCard(text = PrinterInfoTestTags.PRINTER_INFO_NOTES_ONE)

        NoteCard(text = PrinterInfoTestTags.PRINTER_INFO_NOTES_TWO)

        NoteCard(text = PrinterInfoTestTags.PRINTER_INFO_NOTES_THREE)

        NoteCard(text = PrinterInfoTestTags.PRINTER_INFO_NOTES_FOUR)
    }
}