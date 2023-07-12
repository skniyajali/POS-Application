package com.niyaj.popos.features.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.destinations.CartScreenDestination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StandardBackdropScaffold(
    navController : NavController,
    backdropScaffoldState : BackdropScaffoldState,
    scaffoldState : ScaffoldState,
    scope : CoroutineScope,
    selectedOrderId : String? = null,
    showSearchBar : Boolean = false,
    searchText : String = "",
    showFloatingActionButton : Boolean = true,
    showBottomBar: Boolean = true,
    bottomBar : @Composable () -> Unit = { StandardBottomNavigation(navController = navController, true) },
    onSelectedOrderClick : () -> Unit,
    onSearchButtonClick : () -> Unit = {},
    onSearchTextChanged : (String) -> Unit = {},
    onClearClick : () -> Unit = {},
    onBackButtonClick : () -> Unit = {},
    backLayerContent : @Composable () -> Unit,
    frontLayerContent : @Composable () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        drawerContent = {
            StandardDrawer(navController)
        },
        drawerShape = RectangleShape,
        drawerGesturesEnabled = true,
        floatingActionButton = {
            AnimatedVisibility(
                visible = !backdropScaffoldState.isRevealed && showFloatingActionButton && showBottomBar,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    onClick = {
                        navController.navigate(AddEditCartOrderScreenDestination())
                    },
                    shape = CircleShape,
                    modifier = Modifier,
                    contentColor = MaterialTheme.colors.onPrimary,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.create_order)
                    )
                }
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            AnimatedVisibility(
                visible = !backdropScaffoldState.isRevealed && showFloatingActionButton,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { fullHeight ->
                        fullHeight / 4
                    }
                ),
                exit = fadeOut() + slideOutVertically(
                    targetOffsetY = { fullHeight ->
                        fullHeight / 4
                    }
                ),
            ) {
                bottomBar()
            }
        },
    ) { paddingValues ->
        BackdropScaffold(
            appBar = {
                StandardToolbar(
                    modifier = Modifier
                        .fillMaxWidth(),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    scaffoldState.drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(id = R.string.toggle_menu),
                                tint = MaterialTheme.colors.background
                            )
                        }
                    },
                    showBackArrow = showSearchBar,
                    onBackButtonClick = onBackButtonClick,
                    navActions = {
                        if (showSearchBar) {
                            StandardSearchBar(
                                searchText = searchText,
                                placeholderText = "Search for products...",
                                onSearchTextChanged = {
                                    onSearchTextChanged(it)
                                },
                                onClearClick = onClearClick,
                            )
                        } else {
                            IconButton(
                                onClick = onSearchButtonClick
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(id = R.string.search_icon),
                                    tint = MaterialTheme.colors.onPrimary
                                )
                            }

                            IconButton(onClick = {
                                navController.navigate(CartScreenDestination)
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingCart,
                                    contentDescription = stringResource(id = R.string.cart_screen),
                                    tint = MaterialTheme.colors.onPrimary
                                )
                            }
                        }
                    },
                    title = {
                        if (!selectedOrderId.isNullOrEmpty() && !showSearchBar) {
                            SelectedOrder(
                                text = selectedOrderId,
                                onClick = onSelectedOrderClick
                            )
                        }
                    }
                )
            },
            backLayerContent = {
                backLayerContent()
            },
            frontLayerContent = {
                frontLayerContent()
            },
            headerHeight = 0.dp,
            frontLayerBackgroundColor = MaterialTheme.colors.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            scaffoldState = backdropScaffoldState,
        )
    }
}