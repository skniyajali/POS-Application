package com.niyaj.popos.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.popos.R
import com.niyaj.popos.presentation.ui.theme.ButtonSize
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall

@Composable
fun ItemNotAvailable(
    text: String = "",
    buttonText: String = "",
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.body1
            )
            if(buttonText.isNotEmpty()){
                Spacer(modifier = Modifier.height(SpaceMedium))
                Button(
                    onClick = {
                        onClick()
                    },
                    shape= CutCornerShape(4.dp),
                    modifier = Modifier.heightIn(ButtonSize)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.add_icon) )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = buttonText.uppercase(),
                        style = MaterialTheme.typography.button
                    )
                }
            }
        }
    }
}