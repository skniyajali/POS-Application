package com.niyaj.feature.address.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toDate
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Address
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.event.UiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddressDetailsCard(
    state: UiState<Address>,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickEdit: () -> Unit,
) {
    Card(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("EmployeeDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                TextWithIcon(
                    text = "Address Details",
                    icon = Icons.Default.Business,
                    isTitle = true
                )
            },
            trailing = {
                IconButton(
                    onClick = onClickEdit
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Address",
                        tint = MaterialTheme.colors.primary
                    )
                }
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = state,
                    label = "Address State"
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Address Details Not Available",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall),
                                verticalArrangement = Arrangement.spacedBy(SpaceSmall)
                            ) {
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.addressName),
                                    text = "Name - ${state.data.addressName}",
                                    icon = Icons.Default.Business
                                )

                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.shortName),
                                    text = "Short Name - ${state.data.shortName}",
                                    icon = Icons.Default.Home
                                )

                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.createdAt.toDate),
                                    text = "Created At : ${state.data.createdAt.toFormattedDateAndTime}",
                                    icon = Icons.Default.CalendarToday
                                )

                                state.data.updatedAt?.let {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    TextWithIcon(
                                        text = "Updated At : ${it.toFormattedDateAndTime}",
                                        icon = Icons.AutoMirrored.Filled.Login
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}