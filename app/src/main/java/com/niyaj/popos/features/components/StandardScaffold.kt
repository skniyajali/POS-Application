package com.niyaj.popos.features.components

import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.common.utils.Constants.STANDARD_BACK_BUTTON

@Composable
fun StandardScaffold(
    navController : NavController,
    scaffoldState : ScaffoldState,
    modifier : Modifier = Modifier,
    navigationIcon : @Composable () -> Unit = {},
    showBackArrow : Boolean = false,
    showBottomBar : Boolean = false,
    onBackButtonClick : () -> Unit = { navController.navigateUp() },
    navActions : @Composable RowScope.() -> Unit = {},
    title : @Composable () -> Unit = {},
    isFloatingActionButtonDocked : Boolean = false,
    floatingActionButton : @Composable () -> Unit = {},
    floatingActionButtonPosition : FabPosition = FabPosition.Center,
    topAppBarBackgroundColor : Color = MaterialTheme.colors.primary,
    showTopBar : Boolean = true,
    bottomBar : @Composable () -> Unit = {
        StandardBottomNavigation(
            navController,
            isFloatingActionButtonDocked
        )
    },
    content : @Composable (PaddingValues) -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = title,
                    modifier = modifier,
                    navigationIcon = if (showBackArrow) {
                        {
                            IconButton(
                                onClick = {
                                    onBackButtonClick()
                                },
                                modifier = Modifier.testTag(STANDARD_BACK_BUTTON)
                            ) {
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
            }
        },
        bottomBar = {
            if (showBottomBar) {
                bottomBar()
            }
        },
        floatingActionButton = floatingActionButton,
        isFloatingActionButtonDocked = isFloatingActionButtonDocked,
        floatingActionButtonPosition = floatingActionButtonPosition,
    ) {
        content(it)
    }
}

