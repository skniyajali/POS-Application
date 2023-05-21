package com.niyaj.popos.features.cart.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.niyaj.popos.features.cart.presentation.dine_in.DineInScreen
import com.niyaj.popos.features.cart.presentation.dine_out.DineOutScreen
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.util.Tabs
import com.niyaj.popos.features.components.util.TabsContent
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.destinations.CartScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.ResultRecipient
import io.sentry.compose.SentryTraced

/**
 * Cart screen for showing cart item.
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param resultRecipient
 * @see DineOutScreen
 * @see DineInScreen
 */
@Destination
@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class)
@Composable
fun CartScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    resultRecipient: ResultRecipient<AddEditCartOrderScreenDestination, String>
) {
    val pagerState = rememberPagerState()

    SentryTraced(tag = CartScreenDestination.route) {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            navigationIcon = {},
            showBackArrow = true,
            navActions = {
                IconButton(
                    onClick = {
                        navController.navigate(OrderScreenDestination())
                    },
                ){
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = "go to order screen",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            },
            title = {
                Text(text = "My Cart")
            },
        ){
            val tabs = listOf(
                CartTabItem.DineOutItem {
                    DineOutScreen(
                        navController = navController,
                        scaffoldState = scaffoldState,
                        resultRecipient = resultRecipient
                    )
                },
                CartTabItem.DineInItem {
                    DineInScreen(
                        navController = navController,
                        scaffoldState = scaffoldState,
                        resultRecipient = resultRecipient
                    )
                },
            )

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Tabs(tabs = tabs, pagerState = pagerState)

                TabsContent(tabs = tabs, pagerState = pagerState)
            }
        }
    }
}