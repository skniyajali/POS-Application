package com.niyaj.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.HintGray
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall

@Composable
fun ItemNotAvailable(
    modifier: Modifier = Modifier,
    btnModifier: Modifier = Modifier,
    text: String = "",
    buttonText: String = "",
    showImage: Boolean = true,
    icon: ImageVector = Icons.Default.Add,
    @DrawableRes
    image: Int = R.drawable.emptystate,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showImage) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "No data available"
            )

            Spacer(modifier = Modifier.height(SpaceMedium))
        }

        Text(
            modifier = Modifier.padding(SpaceSmall),
            text = text,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            minLines = 2,
            color = HintGray
        )

        if(buttonText.isNotEmpty()){
            Spacer(modifier = Modifier.height(SpaceMedium))
            Button(
                onClick = onClick,
                shape= CutCornerShape(4.dp),
                modifier = btnModifier.heightIn(ButtonSize)
            ) {
                Icon(imageVector = icon, contentDescription = stringResource(id = R.string.add_icon) )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = buttonText.uppercase(),
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}


@Composable
fun ItemNotAvailableHalf(
    modifier: Modifier = Modifier,
    btnModifier: Modifier = Modifier,
    text: String,
    buttonText: String = "",
    showImage: Boolean = true,
    icon: ImageVector = Icons.Default.Add,
    @DrawableRes
    image: Int = R.drawable.emptystate,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showImage) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "No data available"
            )

            Spacer(modifier = Modifier.height(SpaceMedium))
        }

        Text(
            text = text,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            color = HintGray
        )

        if(buttonText.isNotEmpty()){
            Spacer(modifier = Modifier.height(SpaceMedium))
            Button(
                onClick = onClick,
                shape= CutCornerShape(4.dp),
                modifier = btnModifier.heightIn(ButtonSize)
            ) {
                Icon(imageVector = icon, contentDescription = stringResource(id = R.string.add_icon) )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = buttonText.uppercase(),
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}

@Composable
fun ItemNotFound(
    modifier: Modifier = Modifier,
    text: String,
    buttonText: String,
    icon: ImageVector = Icons.Default.Add,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            color = HintGray
        )

        Spacer(modifier = Modifier.height(SpaceMedium))

        StartIconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text.plus("Icon")
            )

            Text(text = buttonText)
        }
    }
}