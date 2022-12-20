package com.niyaj.popos.features.product.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.components.StandardChip
import com.niyaj.popos.util.toRupee

@Composable
fun ProductCard(
    productName: String,
    productPrice: String,
    productCategoryName: String,
    onSelectProduct: () -> Unit = {},
    doesSelected: Boolean = false,
    isAvailable: Boolean = true,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelectProduct()
            },
        shape = RoundedCornerShape(4.dp),
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.onSurface,
        border = if(doesSelected)
            BorderStroke(1.dp, MaterialTheme.colors.primary)
        else if(!isAvailable)
            BorderStroke(1.dp, TextGray)
        else null,
        elevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(SpaceMini))
                Text(
                    text = productPrice.toRupee,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                )
            }

            StandardChip(
                text = productCategoryName,
                onClick = {}
            )
        }
    }
}