package com.niyaj.popos.features.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeLarge
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ExtendedFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.CartScreenDestination
import com.niyaj.popos.features.destinations.EmployeeScreenDestination
import com.niyaj.popos.features.destinations.ExpensesScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.niyaj.popos.features.destinations.ProductScreenDestination
import com.niyaj.popos.features.destinations.ReportScreenDestination
import com.niyaj.popos.features.destinations.UpdateProfileScreenDestination
import com.niyaj.popos.features.main_feed.presentation.components.IconBox
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Destination
@Composable
fun ProfileScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<UpdateProfileScreenDestination, String>
) {

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val info = profileViewModel.info.collectAsState().value
    val isLoading = profileViewModel.isLoading

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = true) {
        profileViewModel.eventFlow.collect { event ->
            when (event) {
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

    resultRecipient.onNavResult {result ->
        when(result){
            is NavResult.Canceled -> { }
            is NavResult.Value -> {
                profileViewModel.onEvent(ProfileEvent.RefreshEvent)

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        title = {
            Text(text = info.name)
        },
        navActions = {
            IconButton(
                onClick = {
                    navController.navigate(UpdateProfileScreenDestination)
                }
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            ExtendedFabButton(
                text = "",
                showScrollToTop = showScrollToTop.value,
                visible = false,
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {},
            )
        },
        floatingActionButtonPosition = if (showScrollToTop.value) FabPosition.End else FabPosition.Center,
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = {
                profileViewModel.onEvent(ProfileEvent.RefreshEvent)
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        elevation = 4.dp,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.only_logo),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .size(
                                        width = ProfilePictureSizeLarge,
                                        height = ProfilePictureSizeMedium
                                    )
                                    .align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(SpaceMini))

                            Text(
                                text = info.name,
                                style = MaterialTheme.typography.h5,
                                fontWeight = FontWeight.Bold,
                            )

                            Spacer(modifier = Modifier.height(SpaceMini))

                            Text(
                                text = info.tagline,
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Medium,
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            Text(
                                text = info.description,
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Medium,
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            Text(
                                text = info.address,
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                minLines = 2,
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = info.primaryPhone,
                                    style = MaterialTheme.typography.body1,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                )
                                Text(text = " / ")
                                Text(
                                    text = info.secondaryPhone,
                                    style = MaterialTheme.typography.body1,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(SpaceMedium))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconBox(
                            modifier = Modifier.width(148.dp),
                            iconName = Icons.Default.ShoppingCart,
                            text = "My Carts",
                            elevation = 2.dp,
                            backgroundColor = MaterialTheme.colors.onPrimary,
                            iconColor = MaterialTheme.colors.secondaryVariant,
                            textColor = MaterialTheme.colors.onBackground,
                            onClick = { navController.navigate(CartScreenDestination) }
                        )
                        Spacer(modifier = Modifier.width(SpaceMedium))
                        IconBox(
                            modifier = Modifier.width(148.dp),
                            iconName = Icons.Default.Inventory,
                            text = "My Orders",
                            elevation = 2.dp,
                            backgroundColor = MaterialTheme.colors.onPrimary,
                            iconColor = MaterialTheme.colors.secondaryVariant,
                            textColor = MaterialTheme.colors.onBackground,
                            onClick = { navController.navigate(OrderScreenDestination()) }
                        )
                    }
                    Spacer(modifier = Modifier.height(SpaceMedium))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconBox(
                            modifier = Modifier.width(148.dp),
                            iconName = Icons.Default.Assessment,
                            text = "Reports",
                            elevation = 2.dp,
                            backgroundColor = MaterialTheme.colors.onPrimary,
                            iconColor = MaterialTheme.colors.secondaryVariant,
                            textColor = MaterialTheme.colors.onBackground,
                            onClick = { navController.navigate(ReportScreenDestination()) }
                        )
                        Spacer(modifier = Modifier.width(SpaceMedium))
                        IconBox(
                            modifier = Modifier.width(148.dp),
                            iconName = Icons.Default.People,
                            text = "Employee",
                            elevation = 2.dp,
                            backgroundColor = MaterialTheme.colors.onPrimary,
                            iconColor = MaterialTheme.colors.secondaryVariant,
                            textColor = MaterialTheme.colors.onBackground,
                            onClick = { navController.navigate(EmployeeScreenDestination) }
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconBox(
                            modifier = Modifier.width(148.dp),
                            iconName = Icons.Default.Money,
                            text = "Expenses",
                            elevation = 2.dp,
                            backgroundColor = MaterialTheme.colors.onPrimary,
                            iconColor = MaterialTheme.colors.secondaryVariant,
                            textColor = MaterialTheme.colors.onBackground,
                            onClick = { navController.navigate(ExpensesScreenDestination()) }
                        )
                        Spacer(modifier = Modifier.width(SpaceMedium))
                        IconBox(
                            modifier = Modifier.width(148.dp),
                            iconName = Icons.Default.Dns,
                            text = "Products",
                            elevation = 2.dp,
                            backgroundColor = MaterialTheme.colors.onPrimary,
                            iconColor = MaterialTheme.colors.secondaryVariant,
                            textColor = MaterialTheme.colors.onBackground,
                            onClick = { navController.navigate(ProductScreenDestination) }
                        )
                    }
                    Spacer(modifier = Modifier.height(SpaceMedium))
                }
            }
        }
    }
}