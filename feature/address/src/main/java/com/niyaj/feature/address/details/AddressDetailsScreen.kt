package com.niyaj.feature.address.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.AddressTestTags.ADDRESS_DETAILS_SCREEN
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.feature.address.components.AddressDetailsCard
import com.niyaj.feature.address.components.RecentOrders
import com.niyaj.feature.address.components.TotalOrderDetailsCard
import com.niyaj.feature.address.destinations.AddEditAddressScreenDestination
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardScaffoldNewF
import com.niyaj.ui.util.Screens
import com.niyaj.ui.util.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

@Destination(route = Screens.ADDRESS_DETAILS_SCREEN)
@Composable
fun AddressDetailsScreen(
    addressId: String,
    onClickOrder: (String) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavController = rememberNavController(),
    viewModel: AddressDetailsViewModel = hiltViewModel(),
) {
    val currentId = navController.currentBackStackEntryAsState()
        .value?.arguments?.getString("addressId") ?: addressId

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val addressState = viewModel.addressDetails.collectAsStateWithLifecycle().value

    val orderDetailsState = viewModel.orderDetails.collectAsStateWithLifecycle().value

    val totalOrdersState = viewModel.totalOrders.collectAsStateWithLifecycle().value

    var detailsExpanded by remember { mutableStateOf(true) }
    var orderExpanded by remember { mutableStateOf(true) }

    StandardScaffoldNewF(
        navController = navController,
        scaffoldState = scaffoldState,
        title = ADDRESS_DETAILS_SCREEN,
        showBackButton = true,
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            )
        },
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmallMax)
        ) {
            item(key = "TotalOrder Details") {
                TotalOrderDetailsCard(details = totalOrdersState)
            }

            item(key = "Address Details") {
                AddressDetailsCard(
                    state = addressState,
                    onExpanded = {
                        detailsExpanded = !detailsExpanded
                    },
                    doesExpanded = detailsExpanded,
                    onClickEdit = {
                        navController.navigate(AddEditAddressScreenDestination(currentId))
                    }
                )
            }

            item(key = "OrderDetails") {
                RecentOrders(
                    orderDetailsState = orderDetailsState,
                    doesExpanded = orderExpanded,
                    onExpanded = {
                        orderExpanded = !orderExpanded
                    },
                    onClickOrder = onClickOrder
                )

                Spacer(modifier = Modifier.height(SpaceMini))
            }
        }
    }
}