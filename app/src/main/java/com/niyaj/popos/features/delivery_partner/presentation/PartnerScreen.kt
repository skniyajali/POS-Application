package com.niyaj.popos.features.delivery_partner.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.ModeStandby
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeSmall
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ExtendedFabButton
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.StandardSearchBar
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.delivery_partner.domain.util.PartnerStatus
import com.niyaj.popos.features.destinations.AddEditPartnerScreenDestination
import com.niyaj.popos.util.toFormattedDateAndTime
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch
import timber.log.Timber


@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Destination
@Composable
fun PartnerScreen(
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    partnerViewModel: PartnerViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditPartnerScreenDestination, String>
) {
    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

    val partners = partnerViewModel.state.collectAsStateWithLifecycle().value.partners
    val filterPartner = partnerViewModel.state.collectAsStateWithLifecycle().value.filterPartner
    val isLoading = partnerViewModel.state.collectAsStateWithLifecycle().value.isLoading
    val hasError = partnerViewModel.state.collectAsStateWithLifecycle().value.error

    val selectedPartnerItem = partnerViewModel.selectedPartner.collectAsStateWithLifecycle().value

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedPartnerItem.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = partnerViewModel.toggledSearchBar.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = true) {
        partnerViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.successMessage)
                }

                is UiEvent.OnError -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.errorMessage)
                }

                is UiEvent.IsLoading -> {
                    Timber.d("Loading.. ${event.isLoading.toString()}")
                }
            }
        }
    }

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

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {
                if(selectedPartnerItem.isNotEmpty()) {
                    partnerViewModel.onPartnerEvent(PartnerEvent.SelectPartner(selectedPartnerItem))
                }
            }
            is NavResult.Value -> {
                if(selectedPartnerItem.isNotEmpty()) {
                    partnerViewModel.onPartnerEvent(PartnerEvent.SelectPartner(selectedPartnerItem))
                }
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    BackHandler(true) {
        if (showSearchBar){
            partnerViewModel.onSearchBarCloseAndClearClick()
        } else if(selectedPartnerItem.isNotEmpty()) {
            partnerViewModel.onPartnerEvent(PartnerEvent.SelectPartner(selectedPartnerItem))
        }else{
            navController.navigateUp()
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = selectedPartnerItem.isEmpty(),
        onBackButtonClick = {
            if (showSearchBar){
                partnerViewModel.onSearchBarCloseAndClearClick()
            }else{
                navController.navigateUp()
            }
        },
        title = {
            if (selectedPartnerItem.isEmpty()) {
                Text(text = "Delivery Partners")
            }
        },
        isFloatingActionButtonDocked = partners.isNotEmpty(),
        floatingActionButton = {
            ExtendedFabButton(
                text = stringResource(id = R.string.create_new_partner).uppercase(),
                showScrollToTop = showScrollToTop.value,
                visible = partners.isNotEmpty() && selectedPartnerItem.isEmpty() && !showSearchBar,
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {
                    navController.navigate(AddEditPartnerScreenDestination())
                },
            )
        },
        floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
        navActions = {
            if(selectedPartnerItem.isNotEmpty()) {
                IconButton(
                    onClick = {
                        navController.navigate(AddEditPartnerScreenDestination(selectedPartnerItem))
                    },
                ){
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Partner Item",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        dialogState.show()
                    },
                    enabled = selectedPartnerItem.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Partner",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
            else if(showSearchBar){
                StandardSearchBar(
                    searchText = partnerViewModel.searchText.collectAsStateWithLifecycle().value,
                    placeholderText = "Search for delivery partners...",
                    onSearchTextChanged = {
                        partnerViewModel.onPartnerEvent(PartnerEvent.OnSearchPartner(it))
                    },
                    onClearClick = {
                        partnerViewModel.onSearchTextClearClick()
                    },
                )
            }
            else {
                if (partners.isNotEmpty()){
                    IconButton(
                        onClick = {
                            partnerViewModel.onPartnerEvent(PartnerEvent.ToggleSearchBar)
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
                                BottomSheetScreen.FilterPartnerScreen(
                                    filterPartner = filterPartner,
                                    onFilterChanged = {
                                        partnerViewModel.onPartnerEvent(
                                            PartnerEvent.OnFilterPartner(
                                                it
                                            )
                                        )
                                    },
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = stringResource(id = R.string.filter_delivery_partner),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if(selectedPartnerItem.isNotEmpty()) {
                IconButton(
                    onClick = {
                        partnerViewModel.onPartnerEvent(
                            PartnerEvent.SelectPartner(
                                selectedPartnerItem
                            )
                        )
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
            dialogState = dialogState,
            buttons = {
                positiveButton(
                    text = "Delete",
                    onClick = {
                        partnerViewModel.onPartnerEvent(
                            PartnerEvent.DeletePartner(
                                selectedPartnerItem
                            )
                        )
                    }
                )
                negativeButton(
                    text = "Cancel",
                    onClick = {
                        dialogState.hide()
                    },
                )
            }
        ) {
            title(text = "Delete partner?")
            message(res = R.string.delete_partner_message)
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = {
                partnerViewModel.onPartnerEvent(PartnerEvent.RefreshPartner)
            }
        ) {
            if(isLoading){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    CircularProgressIndicator()
                }
            } else if (partners.isEmpty() || hasError != null) {
                ItemNotAvailable(
                    text = hasError ?: if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_items_in_partner),
                    buttonText = stringResource(id = R.string.create_new_partner).uppercase(),
                    onClick = {
                        navController.navigate(AddEditPartnerScreenDestination())
                    }
                )
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize()
                ){
                    itemsIndexed(partners){ index, partner ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    partnerViewModel.onPartnerEvent(
                                        PartnerEvent.SelectPartner(partner.partnerId)
                                    )
                                },
                            shape = RoundedCornerShape(4.dp),
                            border = if(selectedPartnerItem == partner.partnerId)
                                BorderStroke(1.dp, MaterialTheme.colors.primary)
                            else null,
                            elevation = 2.dp,
                        ) {
                            StandardExpandable(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall),
                                onExpandChanged = {
                                    partnerViewModel.onPartnerEvent(
                                        PartnerEvent.SelectPartner(partner.partnerId)
                                    )
                                },
                                expanded = selectedPartnerItem == partner.partnerId,
                                leading = {},
                                title = {
                                    Column(
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            text = partner.partnerName,
                                            style = MaterialTheme.typography.body1,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Spacer(modifier = Modifier.height(SpaceMini))

                                        Text(
                                            text = partner.partnerPhone,
                                            style = MaterialTheme.typography.body1,
                                            textAlign = TextAlign.Center,
                                        )

                                    }
                                },
                                trailing = {
                                    Card(
                                        modifier = Modifier
                                            .wrapContentSize(),
                                        backgroundColor = when (partner.partnerStatus) {
                                            PartnerStatus.InActive.partnerStatus -> MaterialTheme.colors.secondary
                                            PartnerStatus.Suspended.partnerStatus -> MaterialTheme.colors.error
                                            else -> MaterialTheme.colors.primary
                                        },
                                        shape = RoundedCornerShape(2.dp),
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .wrapContentWidth()
                                                .padding(SpaceMini),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                        ) {
                                            Text(
                                                text = partner.partnerStatus.uppercase(),
                                                style = MaterialTheme.typography.caption,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colors.onPrimary,
                                            )
                                        }

                                    }
                                },
                                rowClickable = true,
                                expand = { modifier ->
                                    IconButton(
                                        modifier = modifier,
                                        onClick = {
                                            partnerViewModel.onPartnerEvent(
                                                PartnerEvent.SelectPartner(partner.partnerId)
                                            )
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Filled.KeyboardArrowDown,
                                            contentDescription = null)
                                    }
                                },
                                content = {
                                    Column(
                                        modifier = Modifier
                                            .padding(SpaceSmall)
                                    ) {
                                        TextWithIcon(
                                            text = "Username - ${partner.partnerName}",
                                            icon = Icons.Default.Person
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Email - ${partner.partnerEmail}",
                                            icon = Icons.Default.AlternateEmail
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Phone - ${partner.partnerPhone}",
                                            icon = Icons.Default.PhoneAndroid
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Password - ${partner.partnerPassword}",
                                            icon = Icons.Default.Password
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Status - ${partner.partnerStatus}",
                                            icon = Icons.Default.ModeStandby
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Type - ${partner.partnerType}",
                                            icon = Icons.Default.MergeType
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Created At : ${partner.createdAt.toFormattedDateAndTime}",
                                            icon = Icons.Default.AccessTime
                                        )
                                        if(!partner.updatedAt.isNullOrEmpty()){
                                            Spacer(modifier = Modifier.height(SpaceSmall))
                                            TextWithIcon(
                                                text = "Last Login : ${partner.updatedAt!!.toFormattedDateAndTime}",
                                                icon = Icons.Default.Login
                                            )
                                        }
                                    }
                                },
                            )
                        }

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        if(index == partners.size - 1) {
                            Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
                        }
                    }
                }
            }
        }
    }

}