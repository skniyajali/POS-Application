package com.niyaj.popos.presentation.cart.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.flowlayout.FlowRow
import com.niyaj.popos.presentation.components.StandardChip
import com.niyaj.popos.presentation.ui.theme.SpaceMini
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.realm.add_on_items.AddOnItem

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
        FlowRow {
            for (addOnItem in addOnItems){
                StandardChip(
                    modifier = Modifier
                        .padding(SpaceMini),
                    text = addOnItem.itemName,
                    secondaryText = if(addOnItem.itemName.startsWith("Cold")) addOnItem.itemPrice.toString() else null,
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