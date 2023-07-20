package com.niyaj.popos.features.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini

@Composable
fun TextWithIcon(
    modifier : Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    text : String = "",
    icon : ImageVector? = null,
    textColor : Color = MaterialTheme.colors.onSurface,
    tintColor : Color = MaterialTheme.colors.primary,
    isTitle : Boolean = false,
    fontWeight : FontWeight = FontWeight.Normal,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (text.isNotEmpty()) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = tintColor,
                    modifier = iconModifier,
                )
                Spacer(modifier = Modifier.width(SpaceMini))
            }

            Text(
                text = text,
                fontFamily = if (text.startsWith("Email") || text.startsWith("Password")) FontFamily.Monospace else null,
                style = MaterialTheme.typography.body1,
                fontWeight = if (isTitle) FontWeight.SemiBold else fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
            )
        }
    }
}


@Composable
fun TextWithIcon(
    modifier : Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    text : AnnotatedString,
    icon : ImageVector? = null,
    isTitle : Boolean = false,
    fontWeight : FontWeight = FontWeight.Normal,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (text.isNotEmpty()) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text.text,
                    tint = MaterialTheme.colors.primary,
                    modifier = iconModifier,
                )
                Spacer(modifier = Modifier.width(SpaceMini))
            }
            Text(
                text = text,
                fontFamily = if (text.startsWith("Email") || text.startsWith("Password")) FontFamily.Monospace else null,
                style = MaterialTheme.typography.body1,
                fontWeight = if (isTitle) FontWeight.SemiBold else fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}


@Composable
fun TextWithTitle(
    modifier : Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    text : String = "",
    icon : ImageVector? = null,
    style : TextStyle = MaterialTheme.typography.body1,
    fontWeight : FontWeight = FontWeight.SemiBold,
    textColor : Color = MaterialTheme.colors.onSurface,
    tintColor : Color = MaterialTheme.colors.secondaryVariant,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (text.isNotEmpty()) {
            Text(
                text = text,
                style = style,
                fontWeight = fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
            )
            icon?.let {
                Spacer(modifier = Modifier.width(SpaceMini))

                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = tintColor,
                    modifier = iconModifier,
                )
            }
        }
    }
}

@Composable
fun TopBarTitle(
    modifier : Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    text : String,
    icon : ImageVector? = null,
    style : TextStyle = MaterialTheme.typography.h6,
    fontWeight : FontWeight = FontWeight.SemiBold,
    textColor : Color = MaterialTheme.colors.onPrimary,
    tintColor : Color = MaterialTheme.colors.onPrimary,
) {
    Row(
        modifier = modifier,
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = tintColor,
                modifier = iconModifier,
            )

            Spacer(modifier = Modifier.width(SpaceMini))
        }

        Text(
            text = text,
            style = style,
            fontWeight = fontWeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = textColor,
        )
    }
}

@Composable
fun NoteText(
    modifier : Modifier = Modifier,
    iconModifier : Modifier = Modifier.size(SpaceMedium),
    text : String = "",
    icon : ImageVector = Icons.Default.Info,
    color : Color = MaterialTheme.colors.error,
    fontWeight : FontWeight = FontWeight.Normal,
    onClick : () -> Unit = {},
) {
    Row(
        modifier = modifier
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (text.isNotEmpty()) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = iconModifier,
            )
            Spacer(modifier = Modifier.width(SpaceMini))

            Text(
                text = text,
                style = MaterialTheme.typography.caption,
                fontWeight = fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = color,
            )
        }
    }
}