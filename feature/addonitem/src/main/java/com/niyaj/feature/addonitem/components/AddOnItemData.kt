package com.niyaj.feature.addonitem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.AddOnConstants.ADDON_ITEM_TAG
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddOnItem
import com.niyaj.ui.components.CircularBox

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddOnItemData(
    modifier: Modifier = Modifier,
    item: AddOnItem,
    doesSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.secondary),
) = trace("AddOnItemData") {
    val borderStroke = if (doesSelected(item.addOnItemId)) border else null

    Card(
        modifier = modifier
            .testTag(ADDON_ITEM_TAG.plus(item.addOnItemId))
            .padding(SpaceSmall)
            .combinedClickable(
                onClick = {
                    onClick(item.addOnItemId)
                },
                onLongClick = {
                    onLongClick(item.addOnItemId)
                },
            ),
        elevation = 1.dp,
        border = borderStroke
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
                    text = item.itemName,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(text = item.itemPrice.toRupee)
            }

            CircularBox(
                backgroundColor = MaterialTheme.colors.background,
                icon = Icons.Default.Link,
                doesSelected = doesSelected(item.addOnItemId),
                showBorder = !item.isApplicable
            )
        }
    }
}