package com.niyaj.popos.features.order.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.niyaj.popos.features.common.ui.theme.HintGray

@Composable
fun TwoGridTexts(
    modifier : Modifier = Modifier,
    textOne: String = "",
    textTwo: String = "",
    isTitle: Boolean = false,
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            modifier = Modifier.weight(2.5f, true),
            text = textOne,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.SemiBold,
        )

        Text(
            modifier = Modifier.weight(0.5f, true),
            text = textTwo,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.End,
            fontWeight = if(isTitle) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}


@Composable
fun TwoGridText(
    modifier : Modifier = Modifier,
    textOne: String = "",
    textTwo: String = "",
    textColor: Color = HintGray
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            modifier = Modifier,
            text = textOne,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
        )

        Text(
            modifier = Modifier,
            text = textTwo,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Normal,
            color = textColor,
        )
    }
}