package com.niyaj.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.common.utils.Constants.CLEAR_ICON
import com.niyaj.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.core.ui.R

@Composable
fun StandardScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigationIcon: @Composable () -> Unit = {},
    showBackArrow: Boolean = false,
    showBottomBar: Boolean = false,
    onBackButtonClick: () -> Unit = { navController.navigateUp() },
    navActions: @Composable RowScope.() -> Unit = {},
    title: @Composable () -> Unit = {},
    isFloatingActionButtonDocked: Boolean = false,
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.Center,
    topAppBarBackgroundColor: Color = MaterialTheme.colors.primary,
    showTopBar: Boolean = true,
    bottomBar: @Composable () -> Unit = {
        StandardBottomNavigation(
            navController,
            isFloatingActionButtonDocked
        )
    },
    content: @Composable (PaddingValues) -> Unit = {},
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
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            AnimatedVisibility(
                visible = showBottomBar,
                label = "BottomBar",
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { fullHeight ->
                        fullHeight / 4
                    }
                ),
                exit = fadeOut() + slideOutVertically(
                    targetOffsetY = { fullHeight ->
                        fullHeight / 4
                    }
                )
            ) {
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


@Composable
fun StandardScaffoldNew(
    modifier: Modifier = Modifier,
    navController: NavController,
    title: String,
    selectionCount: Int,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = false,
    showBackButton: Boolean = true,
    showFab: Boolean = true,
    fabPosition: FabPosition = FabPosition.Center,
    floatingActionButton: @Composable () -> Unit,
    navActions: @Composable RowScope.() -> Unit,
    bottomBar: @Composable () -> Unit = {
        StandardBottomNavigation(navController, showFab)
    },
    onDeselect: () -> Unit = {},
    onBackClick: () -> Unit = { navController.navigateUp() },
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    content: @Composable (PaddingValues) -> Unit,
) {
    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val selectedState = updateTransition(targetState = selectionCount, label = "selection count")
    val transition = updateTransition(selectionCount != 0, label = "isContextual")

    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if (isContextualMode) {
            MaterialTheme.colors.secondaryVariant
        } else {
            MaterialTheme.colors.primary
        }
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor,
            darkIcons = false,
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            AnimatedVisibility(
                visible = showTopBar,
                label = "TopBar",
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TopAppBar(
                    title = { Text(text = title) },
                    modifier = modifier,
                    navigationIcon = {
                        if (showBackButton) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.testTag(STANDARD_BACK_BUTTON)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                )
                            }
                        } else {
                            AnimatedContent(
                                targetState = selectedState,
                                transitionSpec = {
                                    (fadeIn()).togetherWith(fadeOut(animationSpec = tween(200)))
                                },
                                label = "navigationIcon",
                                contentKey = {
                                    it
                                }
                            ) { state ->
                                if (state.currentState != 0) {
                                    IconButton(
                                        onClick = onDeselect
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = CLEAR_ICON
                                        )
                                    }
                                }
                            }
                        }
                    },
                    actions = navActions,
                    backgroundColor = backgroundColor,
                    elevation = 0.dp,
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                label = "BottomBar",
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { fullHeight ->
                        fullHeight / 4
                    }
                ),
                exit = fadeOut() + slideOutVertically(
                    targetOffsetY = { fullHeight ->
                        fullHeight / 4
                    }
                )
            ) {
                bottomBar()
            }
        },
        floatingActionButton = floatingActionButton,
        isFloatingActionButtonDocked = showFab,
        floatingActionButtonPosition = fabPosition,
    ) {
        content(it)
    }
}


@Composable
fun StandardScaffoldNewF(
    currentRoute: String,
    modifier: Modifier = Modifier,
    title: String,
    showBackButton: Boolean = false,
    showBottomBar: Boolean = false,
    showFab: Boolean = false,
    fabPosition: FabPosition = FabPosition.Center,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    topBarColor: Color = MaterialTheme.colors.primary,
    onBackClick: () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    navActions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    onNavigateToDestination: (String) -> Unit,
    bottomBar: @Composable () -> Unit = {
        StandardBottomNavigation(
            currentRoute,
            showFab,
            onNavigateToDestination
        )
    },
    content: @Composable (PaddingValues) -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title)
                },
                modifier = modifier,
                navigationIcon = if (showBackButton) {
                    {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag(STANDARD_BACK_BUTTON)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back),
                                tint = MaterialTheme.colors.onPrimary
                            )
                        }
                    }
                } else navigationIcon,
                actions = navActions,
                backgroundColor = topBarColor,
                elevation = 0.dp,
            )
        },
        bottomBar = {
            if (showBottomBar) {
                bottomBar()
            }
        },
        floatingActionButton = floatingActionButton,
        isFloatingActionButtonDocked = showFab,
        floatingActionButtonPosition = fabPosition,
    ) {
        content(it)
    }
}


@Composable
fun StandardScaffoldNewF(
    modifier: Modifier = Modifier,
    navController: NavController,
    title: String,
    showBackButton: Boolean = false,
    showBottomBar: Boolean = false,
    showFab: Boolean = false,
    fabPosition: FabPosition = FabPosition.Center,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    topBarColor: Color = MaterialTheme.colors.primary,
    onBackClick: () -> Unit = {navController.navigateUp()},
    navigationIcon: @Composable () -> Unit = {},
    navActions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {
        StandardBottomNavigation(navController, showFab)
    },
    content: @Composable (PaddingValues) -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title)
                },
                modifier = modifier,
                navigationIcon = if (showBackButton) {
                    {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag(STANDARD_BACK_BUTTON)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back),
                                tint = MaterialTheme.colors.onPrimary
                            )
                        }
                    }
                } else navigationIcon,
                actions = navActions,
                backgroundColor = topBarColor,
                elevation = 0.dp,
            )
        },
        bottomBar = {
            if (showBottomBar) {
                bottomBar()
            }
        },
        floatingActionButton = floatingActionButton,
        isFloatingActionButtonDocked = showFab,
        floatingActionButtonPosition = fabPosition,
    ) {
        content(it)
    }
}