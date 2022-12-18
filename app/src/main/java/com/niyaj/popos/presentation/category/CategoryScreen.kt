package com.niyaj.popos.presentation.category

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.destinations.AddEditCategoryScreenDestination
import com.niyaj.popos.domain.util.BottomSheetScreen
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.ExtendedFabButton
import com.niyaj.popos.presentation.components.ItemNotAvailable
import com.niyaj.popos.presentation.components.StandardScaffold
import com.niyaj.popos.presentation.components.StandardSearchBar
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.presentation.ui.theme.TextGray
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Destination
@Composable
fun CategoryScreen(
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    navController: NavController,
    scaffoldState: ScaffoldState,
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditCategoryScreenDestination, String>
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyGridState()
    val deleteCategoryState = rememberMaterialDialogState()

    val categories = categoryViewModel.categories.collectAsState().value.categories
    val isLoading: Boolean = categoryViewModel.categories.collectAsState().value.isLoading
    val error = categoryViewModel.categories.collectAsState().value.error

    val selectedCategories by lazy {
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

    val showSearchBar by categoryViewModel.toggledSearchBar.collectAsState()

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
                is UiEvent.OnSuccess -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage
                    )
                }

                is UiEvent.OnError -> {
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
                    categoryViewModel.onCategoryEvent(CategoryEvent.DeselectCategories)
                }
            }
            is NavResult.Value -> {
                if (selectedCategories.isNotEmpty()){
                    categoryViewModel.onCategoryEvent(CategoryEvent.DeselectCategories)
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
            categoryViewModel.onCategoryEvent(CategoryEvent.DeselectCategories)
        }else{
            navController.navigateUp()
        }
    }

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
            ExtendedFabButton(
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
            if(selectedCategories.isNotEmpty()) {
                if(selectedCategories.size == 1){
                    IconButton(
                        onClick = {
                            navController.navigate(AddEditCategoryScreenDestination(categoryId = selectedCategories.first()))
                        },
                    ){
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Category",
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }

                IconButton(
                    onClick = {
                        deleteCategoryState.show()
                    },
                    enabled = selectedCategories.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Category",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        categoryViewModel.onCategoryEvent(CategoryEvent.SelectAllCategories)
                    },
                    enabled = selectedCategories.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Rule,
                        contentDescription = "Select All Category",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
            else if(showSearchBar){
                StandardSearchBar(
                    searchText = categoryViewModel.searchText.collectAsState().value,
                    placeholderText = "Search for products",
                    onSearchTextChanged = {
                        categoryViewModel.onCategoryEvent(CategoryEvent.OnSearchCategory(it))
                    },
                    onClearClick = {
                        categoryViewModel.onSearchTextClearClick()
                    },
                )
            }
            else {
                if (categories.isNotEmpty()){
                    IconButton(
                        onClick = {
                            categoryViewModel.onCategoryEvent(CategoryEvent.ToggleSearchBar)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search_icon),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                    IconButton(
                        onClick = {
                            onOpenSheet(
                                BottomSheetScreen.FilterCategoryScreen(
                                    filterCategory = categoryViewModel.categories.value.filterCategory,
                                    onFilterChanged = {
                                        categoryViewModel.onCategoryEvent(CategoryEvent.OnFilterCategory(it))
                                    },
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = stringResource(id = R.string.filter_product),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if(selectedCategories.isNotEmpty()) {
                IconButton(
                    onClick = {
                        categoryViewModel.onCategoryEvent(CategoryEvent.DeselectCategories)
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
                        categoryViewModel.onCategoryEvent(CategoryEvent.DeleteCategories(selectedCategories.toList()))
                    }
                )
                negativeButton(
                    text = "Cancel",
                    onClick = {
                        deleteCategoryState.hide()
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
                categoryViewModel.onCategoryEvent(CategoryEvent.RefreshCategory)
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
                        items(categories) { category ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall)
                                    .clickable {
                                        categoryViewModel.onCategoryEvent(CategoryEvent.SelectCategory(
                                            category.categoryId))
                                    },
                                shape = RoundedCornerShape(4.dp),
                                border = if(selectedCategories.contains(category.categoryId))
                                    BorderStroke(1.dp, MaterialTheme.colors.primary)
                                else if(!category.categoryAvailability)
                                    BorderStroke(1.dp, TextGray)
                                else null,
                                elevation = 2.dp,
                            ) {
                                Text(
                                    text = category.categoryName,
                                    style = MaterialTheme.typography.body1,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(SpaceMedium)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}