package com.niyaj.feature.cart.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddOnItem
import com.niyaj.ui.components.StandardOutlinedChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CartAddOnItems(
    addOnItems: List<AddOnItem> = emptyList(),
    selectedAddOnItem: List<String> = emptyList(),
    onClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
        ) {
            for (addOnItem in addOnItems) {
                StandardOutlinedChip(
                    modifier = Modifier
                        .padding(SpaceMini),
                    text = addOnItem.itemName,
                    secondaryText = if (addOnItem.itemName.startsWith("Cold")) addOnItem.itemPrice.toString() else null,
                    isSelected = selectedAddOnItem.contains(addOnItem.addOnItemId),
                    onClick = {
                        onClick(addOnItem.addOnItemId)
                    }
                )
                Spacer(modifier = Modifier.width(SpaceMini))
            }
        }
    }
}