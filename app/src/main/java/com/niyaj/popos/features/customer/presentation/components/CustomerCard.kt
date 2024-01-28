package com.niyaj.popos.features.customer.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.common.ui.theme.LightColor8
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeSmall
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.customer.domain.model.Customer

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomerCard(
    modifier: Modifier = Modifier,
    customer : Customer,
    doesSelected: (String) -> Boolean,
    doesAnySelected: Boolean = false,
    onSelectContact: (String) -> Unit,
    onClickViewDetails: (String) -> Unit = {},
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceMini)
            .testTag(customer.customerPhone)
            .combinedClickable(
                onClick = {
                    if (!doesAnySelected) {
                        onClickViewDetails(customer.customerId)
                    } else {
                        onSelectContact(customer.customerId)
                    }
                },
                onLongClick = {
                    onSelectContact(customer.customerId)
                }
            ),
        border = if(doesSelected(customer.customerId))
            BorderStroke(1.dp, MaterialTheme.colors.primary)
        else null,
        elevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            customer.customerName?.let { it ->
                TextWithIcon(
                    text = it,
                    icon = Icons.Default.Person,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(SpaceMini))
            }

            TextWithIcon(
                text = customer.customerPhone,
                icon = Icons.Default.PhoneAndroid,
                fontWeight = FontWeight.SemiBold,
            )

            customer.customerEmail?.let { email ->
                Spacer(modifier = Modifier.height(SpaceMini))
                TextWithIcon(
                    text = email,
                    icon = Icons.Default.Email,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
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
                        CustomerCard(
                            customer = customer,
                            doesSelected = {
                                selectedCustomers.contains(it)
                            },
                            doesAnySelected = true,
                            onSelectContact = {
                                onSelectCustomer(customer.customerId)
                            },
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