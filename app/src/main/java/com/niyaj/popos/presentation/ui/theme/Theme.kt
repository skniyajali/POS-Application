package com.niyaj.popos.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color.Black,
    surface = Cream2,
    onPrimary = Color.Black,
    onSecondary = GreenAccent,
    onBackground = Color.Black,
    onSurface = Color.White,
)

private val LightColorPalette = lightColors(
//    primary = Purple500,
    primary = Primary,
    primaryVariant = PrimaryVariant,
    onPrimary = Color.White,

//    secondary = Olive,
    secondary = PrimaryVariant,
    secondaryVariant = Olive,
    onSecondary = Color.White,

//    background = LightColor8,
    background = BackgroundColor,
    onBackground = Color.Black,

//    surface = Cream2,
    surface = OnBackgroundColor,
    onSurface = Color.Black,

    error = Color.Red,
    onError = Color.White,
)

@Composable
fun PoposTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val color = if (darkTheme) {
        DarkColorPalette
        // PoposLightColorPalette
        LightColorPalette
    } else {

        LightColorPalette
        //PoposLightColorPalette
    }

    MaterialTheme(
        colors = color,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}