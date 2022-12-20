package com.niyaj.popos.features.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.niyaj.popos.R


@Composable
fun StandardToolbar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit =  {},
    showBackArrow: Boolean = false,
    onBackButtonClick: () -> Unit = {},
    navActions: @Composable RowScope.() -> Unit = {},
    title: @Composable () -> Unit = {},
) {

    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = if(showBackArrow) {
            {
                IconButton(onClick = {
                    onBackButtonClick()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        } else navigationIcon,
        actions = navActions,
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 0.dp,
    )
}