package com.niyaj.feature.charges.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.ChargesTestTags
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Charges
import com.niyaj.ui.components.CircularBox


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChargesData(
    modifier: Modifier = Modifier,
    item: Charges,
    doesSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    borderStroke: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant)
) = trace("ChargesData") {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .testTag(ChargesTestTags.CHARGES_TAG.plus(item.chargesId))
            .combinedClickable(
                onClick = {
                    onClick(item.chargesId)
                },
                onLongClick = {
                    onLongClick(item.chargesId)
                }
            ),
        border = if (doesSelected(item.chargesId)) borderStroke else null,
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.chargesName,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(text = item.chargesPrice.toRupee)
            }

            CircularBox(
                icon = Icons.Default.Bolt,
                doesSelected = doesSelected(item.chargesId),
                showBorder = !item.isApplicable
            )
        }
    }
}