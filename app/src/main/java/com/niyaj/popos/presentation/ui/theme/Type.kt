package com.niyaj.popos.presentation.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.niyaj.popos.R

private val fontFamily = FontFamily(
    //Satoshi
//    Font(R.font.satoshi_light, FontWeight.Light),
//    Font(R.font.satoshi_regular, FontWeight.Normal),
//    Font(R.font.satoshi_medium, FontWeight.Medium),
//    Font(R.font.satoshi_bold, FontWeight.Bold),
//    Font(R.font.satoshi_black, FontWeight.Black),


    // Panton
//    Font(R.font.panton_light, FontWeight.Light),
//    Font(R.font.panton_regular, FontWeight.Normal),
//    Font(R.font.panton_regular, FontWeight.Medium),
//    Font(R.font.panton_bold, FontWeight.Bold),
//    Font(R.font.panton_black, FontWeight.Black),
//    Font(R.font.panton_semi_bold, FontWeight.SemiBold),

    // Panton Narrow
    Font(R.font.panton_narrow_light, FontWeight.Light),
    Font(R.font.panton_narrow_regular, FontWeight.Normal),
    Font(R.font.panton_narrow_regular, FontWeight.Medium),
    Font(R.font.panton_narrow_bold, FontWeight.Bold),
    Font(R.font.panton_narrow_black, FontWeight.Black),
    Font(R.font.panton_narrow_semi_bold, FontWeight.SemiBold),
    Font(R.font.panton_narrow_extra_bold, FontWeight.ExtraBold),


//    //Nexa
//    Font(R.font.nexa_light, FontWeight.Light),
//    Font(R.font.nexa_regular, FontWeight.Normal),
//    Font(R.font.nexa_book, FontWeight.Medium),
//    Font(R.font.nexa_bold, FontWeight.Bold),
//    Font(R.font.nexa_black, FontWeight.Black),

    //Nexa
//    Font(R.font.nexa_text_light, FontWeight.Light),
//    Font(R.font.nexa_text_regular, FontWeight.Normal),
//    Font(R.font.nexa_text_book, FontWeight.Medium),
//    Font(R.font.nexa_text_bold, FontWeight.Bold),
//    Font(R.font.nexa_text_black, FontWeight.Black),


    //JetBrains
//    Font(R.font.jet_brains_mono_light, FontWeight.Light),
//    Font(R.font.jet_brains_mono_regular, FontWeight.Normal),
//    Font(R.font.jet_brains_mono_medium, FontWeight.Medium),
//    Font(R.font.jet_brains_mono_bold, FontWeight.Bold),
//    Font(R.font.jet_brains_mono_bold, FontWeight.Black),
//    Font(R.font.jet_brains_mono_semi_bold, FontWeight.SemiBold),
//    Font(R.font.jet_brains_mono_extra_bold, FontWeight.ExtraBold),

    )

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 96.sp,
        letterSpacing = (-1.5).sp
    ),
    h2 = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 60.sp,
        letterSpacing = (-0.5).sp
    ),
    h3 = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        letterSpacing = 0.sp
    ),
    h4 = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        letterSpacing = 0.25.sp
    ),
    h5 = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        letterSpacing = 0.15.sp
    ),
    h6 = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    body1 = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    button = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 1.5.sp
    ),
    caption = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    ),
    overline = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    ),
)