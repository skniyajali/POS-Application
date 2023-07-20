package com.niyaj.popos.features.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.Text
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

/**
 * A composable function that creates a backdrop scaffold layout.
 *
 * @param navController The NavController instance used for navigation within the app.
 * @param backdropScaffoldState The state object that controls the backdrop scaffold behavior.
 * @param scaffoldState The state object that controls the scaffold behavior.
 * @param scope The CoroutineScope used to launch coroutines for asynchronous operations.
 * @param selectedOrderId The selected order ID, or null if no order is selected. Default is null.
 * @param showSearchBar Whether to show the search bar. Default is false.
 * @param searchText The current text in the search bar. Default is an empty string.
 * @param showFloatingActionButton Whether to show the floating action button. Default is true.
 * @param showBottomBar Whether to show the bottom bar. Default is false.
 * @param bottomBar The composable function representing the content of the bottom bar.
 *                   Default is [StandardBottomNavigation] with the NavController and true as parameters.
 * @param onSelectedOrderClick Callback when the selected order is clicked.
 * @param onSearchButtonClick Callback when the search button is clicked.
 * @param onSearchTextChanged Callback when the search text is changed.
 * @param onClearClick Callback when the clear button is clicked.
 * @param onBackButtonClick Callback when the back button is clicked.
 * @param backLayerContent The composable function representing the content of the back layer.
 * @param frontLayerContent The composable function representing the content of the front layer.
 */
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
    showBottomBar : Boolean = false,
    bottomBar : @Composable () -> Unit = { StandardBottomNavigation(navController, true) },
    onSelectedOrderClick : () -> Unit,
    onSearchButtonClick : () -> Unit = {},
    onSearchTextChanged : (String) -> Unit = {},
    onClearClick : () -> Unit = {},
    onBackButtonClick : () -> Unit = {},
    backLayerContent : @Composable (PaddingValues) -> Unit,
    frontLayerContent : @Composable (PaddingValues) -> Unit,
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
                visible = !backdropScaffoldState.isRevealed && showFloatingActionButton,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                if (showBottomBar) {
                    FloatingActionButton(
                        shape = CircleShape,
                        onClick = {
                            navController.navigate(AddEditCartOrderScreenDestination())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.create_order)
                        )
                    }
                } else {
                    ExtendedFloatingActionButton(
                        text = {
                            Text(text = stringResource(id = R.string.create_new_order).uppercase())
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = R.string.create_order)
                            )
                        },
                        onClick = {
                            navController.navigate(AddEditCartOrderScreenDestination())
                        }
                    )
                }
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar && (!backdropScaffoldState.isRevealed && showFloatingActionButton),
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
                backLayerContent(paddingValues)
            },
            frontLayerContent = {
                frontLayerContent(paddingValues)
            },
            headerHeight = 0.dp,
            frontLayerBackgroundColor = MaterialTheme.colors.background,
            modifier = Modifier
                .fillMaxSize(),
            scaffoldState = backdropScaffoldState,
        )
    }
}