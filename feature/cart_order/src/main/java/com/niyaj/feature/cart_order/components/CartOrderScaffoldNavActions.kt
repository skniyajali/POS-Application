package com.niyaj.feature.cart_order.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_SEARCH_PLACEHOLDER
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.cart_order.CartOrderScreen
import com.niyaj.ui.components.ScaffoldNavActions


/**
 * [ScaffoldNavActions] for [CartOrderScreen]
 */
@Composable
fun CartOrderScaffoldNavActions(
    selectionCount: Int,
    showSearchIcon: Boolean,
    showSearchBar: Boolean,
    searchText: String,
    showMenu: Boolean,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onToggleMenu: () -> Unit,
    onDismissDropdown: () -> Unit,
    onDropItemClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSelectOrderClick: () -> Unit,
    onSelectAllClick: () -> Unit,
) = trace("CartOrderScaffoldNavActions") {
    ScaffoldNavActions(
        placeholderText = CART_ORDER_SEARCH_PLACEHOLDER,
        selectionCount = selectionCount,
        showSearchIcon = showSearchIcon,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onSelectAllClick = onSelectAllClick,
        showSearchBar = showSearchBar,
        searchText = searchText,
        onSearchTextChanged = onSearchTextChanged,
        onClearClick = onClearClick,
        onSearchClick = onSearchClick,
        showSettingsIcon = true,
        onSettingsClick = onSettingsClick,
        content = {
            Box {
                IconButton(
                    onClick = onToggleMenu,
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "View More Settings",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = onDismissDropdown,
                ) {
                    DropdownMenuItem(
                        onClick = onDropItemClick
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "View All",
                                tint = MaterialTheme.colors.secondaryVariant
                            )
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = "View All",
                                style = MaterialTheme.typography.body1,
                            )
                        }
                    }
                }
            }
        },
        preActionContent = {
            AnimatedVisibility(
                visible = selectionCount == 1
            ) {
                IconButton(
                    onClick = onSelectOrderClick
                ) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = "Select Order",
                    )
                }
            }
        },
    )
}