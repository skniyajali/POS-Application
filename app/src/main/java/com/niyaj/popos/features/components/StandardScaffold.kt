package com.niyaj.popos.features.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niyaj.popos.R

@Composable
fun StandardScaffold(
    navController: NavController,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit =  {},
    showBackArrow: Boolean = false,
    onBackButtonClick: () -> Unit = {navController.navigateUp()},
    navActions: @Composable RowScope.() -> Unit = {},
    title: @Composable () -> Unit = {},
    isFloatingActionButtonDocked : Boolean = false,
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.Center,
    topAppBarBackgroundColor: Color = MaterialTheme.colors.primary,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
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
                backgroundColor = topAppBarBackgroundColor,
                elevation = 0.dp,
            )
        },
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        isFloatingActionButtonDocked = isFloatingActionButtonDocked,
        floatingActionButtonPosition = floatingActionButtonPosition,

    ) {
        content()
    }
}

