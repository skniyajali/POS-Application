package com.niyaj.popos.features.order.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ThreeGridTexts(
    textOne: String = "",
    textTwo: String = "",
    textThree: String = "",
    isTitle: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            modifier = Modifier.weight(2f),
            text = textOne,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.SemiBold,
        )

        Text(
            modifier = Modifier.weight(0.5f, true),
            text = textTwo,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Start,
            fontWeight = if(isTitle) FontWeight.SemiBold else FontWeight.Normal,
        )

        Text(
            modifier = Modifier.weight(0.5f, true),
            text = textThree,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.End,
            fontWeight = if(isTitle) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}