package com.niyaj.popos.features.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall


/**
 *
 */
@Composable
fun ImportExportHeader(
    modifier : Modifier = Modifier,
    text: String = "Export selected header",
    isChosen: Boolean = false,
    onClickAll: () -> Unit = {},
    onClickChoose: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.width(SpaceMini))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StandardOutlinedChip(
                    text = "All",
                    isToggleable = true,
                    isSelected = !isChosen,
                    onClick = onClickAll
                )

                Spacer(modifier = Modifier.width(SpaceMini))

                StandardOutlinedChip(
                    text = "Choose",
                    isToggleable = true,
                    isSelected = isChosen,
                    onClick = onClickChoose
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(SpaceSmall))
}


/**
 *
 */
@Composable
fun ImportFooter(
    modifier : Modifier = Modifier,
    importButtonText : String,
    noteText: String,
    importedDataIsEmpty : Boolean = false,
    showImportedBtn : Boolean = false,
    onClearImportedData : () -> Unit,
    onImportData : () -> Unit,
    onOpenFile : () -> Unit,
) {
    if (importedDataIsEmpty) {
        Spacer(modifier = Modifier.height(SpaceMedium))

        Column(
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StandardOutlinedButtonFW(
                text = "Cancel",
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colors.error
                ),
                onClick = onClearImportedData
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardButtonFW(
                text = importButtonText,
                icon = Icons.Default.SaveAlt,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondaryVariant
                ),
                enabled = showImportedBtn,
                onClick = onImportData
            )
        }
    } else {
        NoteCard(text = noteText)

        Spacer(modifier = Modifier.height(SpaceMedium))

        StandardButtonFW(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.open_file),
            icon = Icons.Default.UploadFile,
            onClick = onOpenFile
        )
    }
}


/**
 *
 */
@Composable
fun ExportedFooter(
    text : String,
    showFileSelector: Boolean,
    onExportClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(SpaceMedium))

    StandardButtonFW(
        text = text.uppercase(),
        icon = Icons.Default.SaveAlt,
        iconModifier = Modifier.rotate(180F),
        onClick = onExportClick,
        enabled = showFileSelector,
    )
}