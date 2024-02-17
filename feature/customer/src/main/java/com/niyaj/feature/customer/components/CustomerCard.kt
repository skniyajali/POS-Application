package com.niyaj.feature.customer.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.CustomerTestTags
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.ProfilePictureSizeSmall
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Customer
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithIcon

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun CustomerData(
    modifier: Modifier = Modifier,
    item: Customer,
    doesSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.secondary),
) = trace("CustomerData") {
    val borderStroke = if (doesSelected(item.customerId)) border else null

    ListItem(
        modifier = modifier
            .testTag(CustomerTestTags.CUSTOMER_TAG.plus(item.customerId))
            .fillMaxWidth()
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier.border(it, RoundedCornerShape(SpaceMini))
            } ?: Modifier)
            .shadow(1.dp, RoundedCornerShape(SpaceMini))
            .clip(RoundedCornerShape(SpaceMini))
            .background(MaterialTheme.colors.surface)
            .combinedClickable(
                onClick = {
                    onClick(item.customerId)
                },
                onLongClick = {
                    onLongClick(item.customerId)
                },
            ),
        text = {
            Text(
                text = item.customerPhone,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold
            )
        },
        secondaryText = item.customerName?.let {
            { Text(text = it) }
        },
        icon = {
            CircularBox(
                icon = Icons.Default.Person,
                doesSelected = doesSelected(item.customerId),
                text = item.customerName
            )
        },
        trailing = {
            Icon(
                Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = "Localized description",
            )
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImportExportCustomerBody(
    lazyListState: LazyListState,
    customers: List<Customer>,
    selectedCustomers: List<String>,
    expanded: Boolean,
    onExpandChanged: () -> Unit,
    onSelectCustomer : (String) -> Unit,
    onClickSelectAll: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        backgroundColor = LightColor8,
    ) {
        StandardExpandable(
            onExpandChanged = {
                onExpandChanged()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = expanded,
            title = {
                TextWithIcon(
                    text = if(selectedCustomers.isNotEmpty()) "${selectedCustomers.size} Selected" else "Select Customers",
                    icon = Icons.Default.Dns,
                    isTitle = true
                )
            },
            rowClickable = true,
            trailing = {
                IconButton(
                    onClick = onClickSelectAll
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Rule,
                        contentDescription = "Select All Customers"
                    )
                }
            },
            expand = {  modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                LazyColumn(
                    state = lazyListState,
                ){
                    itemsIndexed(
                        items = customers,
                        key = { index, item ->
                            item.customerId.plus(index)
                        }
                    ){ index, customer ->
                        CustomerData(
                            item = customer,
                            doesSelected = {
                                selectedCustomers.contains(it)
                            },
                            onClick = onSelectCustomer,
                            onLongClick = onSelectCustomer
                        )

                        Spacer(modifier = Modifier.height(SpaceMini))

                        if(index == customers.size - 1) {
                            Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
                        }
                    }
                }
            }
        )
    }

    Spacer(modifier = Modifier.height(SpaceMedium))
}