package com.niyaj.popos.features.category.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.ScaffoldNavActions
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.AddEditCategoryScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Category Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param categoryViewModel
 * @param resultRecipient
 * @see CategoryViewModel
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Destination
@Composable
fun CategoryScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditCategoryScreenDestination, String>
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyGridState()
    val deleteCategoryState = rememberMaterialDialogState()

    val categories = categoryViewModel.categories.collectAsStateWithLifecycle().value.categories
    val isLoading: Boolean = categoryViewModel.categories.collectAsStateWithLifecycle().value.isLoading
    val error = categoryViewModel.categories.collectAsStateWithLifecycle().value.error

    val selectedCategories = remember {
        categoryViewModel.selectedCategories
    }

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedCategories.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = categoryViewModel.toggledSearchBar.collectAsStateWithLifecycle().value
    val searchText = categoryViewModel.searchText.collectAsStateWithLifecycle().value

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = true){
        categoryViewModel.eventFlow.collectLatest{ event ->
            when(event){
                is UiEvent.Success -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage
                    )
                }

                is UiEvent.Error -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage
                    )
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {
                if (selectedCategories.isNotEmpty()){
                    categoryViewModel.onEvent(CategoryEvent.DeselectCategories)
                }
            }
            is NavResult.Value -> {
                if (selectedCategories.isNotEmpty()){
                    categoryViewModel.onEvent(CategoryEvent.DeselectCategories)
                }

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    BackHandler(true) {
        if (showSearchBar){
            categoryViewModel.onSearchBarCloseAndClearClick()
        } else if(selectedCategories.isNotEmpty()) {
            categoryViewModel.onEvent(CategoryEvent.DeselectCategories)
        }else{
            navController.navigateUp()
        }
    }
    
    SentryTraced(tag = "CategoryScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = selectedCategories.isEmpty(),
            onBackButtonClick = {
                if (showSearchBar){
                    categoryViewModel.onSearchBarCloseAndClearClick()
                }else{
                    navController.navigateUp()
                }
            },
            title = {
                if(selectedCategories.isEmpty()){
                    Text(text = "Categories")
                }else if(selectedCategories.size > 1){
                    Text(text = "${selectedCategories.size} Selected")
                }
            },
            isFloatingActionButtonDocked = categories.isNotEmpty(),
            floatingActionButton = {
                StandardFabButton(
                    text = stringResource(id = R.string.create_category).uppercase(),
                    showScrollToTop = showScrollToTop.value,
                    visible = categories.isNotEmpty() && selectedCategories.isEmpty() && !showSearchBar,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(index = 0)
                        }
                    },
                    onClick = {
                        navController.navigate(AddEditCategoryScreenDestination())
                    },
                )
            },
            floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
            navActions = {
                ScaffoldNavActions(
                    multiSelect = true,
                    allItemsIsEmpty = categories.isEmpty(),
                    selectedItems = selectedCategories,
                    onClickEdit = {
                        navController.navigate(AddEditCategoryScreenDestination(categoryId = selectedCategories.first()))
                    },
                    onClickDelete = {
                        deleteCategoryState.show()
                    },
                    showSearchBar = showSearchBar,
                    searchText = searchText,
                    onSearchTextChanged = {
                        categoryViewModel.onEvent(CategoryEvent.OnSearchCategory(it))
                    },
                    onClearClick = {
                        categoryViewModel.onSearchTextClearClick()
                    },
                    onClickSearch = {
                        categoryViewModel.onEvent(CategoryEvent.ToggleSearchBar)
                    },
                    onClickSelectAll = {
                        categoryViewModel.onEvent(CategoryEvent.SelectAllCategories)
                    },
                    showSettingsIcon = false,
                )
            },
            navigationIcon = {
                if(selectedCategories.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            categoryViewModel.onEvent(CategoryEvent.DeselectCategories)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close_icon),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            },
            topAppBarBackgroundColor = backgroundColor,
        ) {
            MaterialDialog(
                dialogState = deleteCategoryState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            categoryViewModel.onEvent(
                                CategoryEvent.DeleteCategories(
                                    selectedCategories.toList()
                                )
                            )
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            deleteCategoryState.hide()
                            categoryViewModel.onEvent(CategoryEvent.DeselectCategories)
                        },
                    )
                }
            ) {
                title(text = "Delete ${selectedCategories.size} Category?")
                message(res = R.string.delete_category_message)
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    categoryViewModel.onEvent(CategoryEvent.RefreshCategory)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall),
                ) {
                    if(isLoading){
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            CircularProgressIndicator()
                        }
                    } else if (categories.isEmpty() || error != null) {
                        ItemNotAvailable(
                            text = error ?: if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_items_in_category),
                            buttonText = stringResource(id = R.string.create_category).uppercase(),
                            onClick = {
                                navController.navigate(AddEditCategoryScreenDestination())
                            }
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = lazyListState,
                        ){
                            items(
                                items = categories,
                                key = {
                                    it.categoryId
                                }
                            ) { category ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceMini)
                                        .combinedClickable(
                                            enabled = true,
                                            onClick = {
                                                if (selectedCategories.isNotEmpty()) {
                                                    categoryViewModel.onEvent(
                                                        CategoryEvent.SelectCategory(category.categoryId)
                                                    )
                                                }
                                            },
                                            onLongClick = {
                                                categoryViewModel.onEvent(
                                                    CategoryEvent.SelectCategory(category.categoryId)
                                                )
                                            },
                                        ),
                                    elevation = 2.dp,
                                    border = if(selectedCategories.contains(category.categoryId)) BorderStroke(1.dp, MaterialTheme.colors.primary)
                                    else if (!category.categoryAvailability) BorderStroke(1.dp, TextGray)
                                    else null
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(SpaceSmall),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(
                                                text = category.categoryName,
                                                style = MaterialTheme.typography.body1,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colors.background),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Category,
                                                contentDescription = category.categoryName,
                                                tint = MaterialTheme.colors.secondaryVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}