package com.niyaj.feature.cart

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.feature.cart.dine_in.DineInScreen
import com.niyaj.feature.cart.dine_out.DineOutScreen
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.util.CartTabItem
import com.niyaj.ui.util.Screens
import com.niyaj.ui.util.Tabs
import com.niyaj.ui.util.TabsContent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

/**
 * Cart screen for showing cart item.
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @see DineOutScreen
 * @see DineInScreen
 */
@RootNavGraph(start = true)
@Destination(route = Screens.CART_SCREEN)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartScreen(
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onClickEditOrder: (String) -> Unit,
    onClickViewOrder: (String) -> Unit,
) {
    val pagerState = rememberPagerState { 2 }

    StandardScaffold(
        navController = navController,
        navigationIcon = {},
        showBackArrow = true,
        navActions = {
            IconButton(
                onClick = {
                    navController.navigate(Screens.ORDER_SCREEN)
                },
            ) {
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
    ) {
        val tabs = listOf(
            CartTabItem.DineOutItem {
                DineOutScreen(
                    navController = navController,
                    scaffoldState = scaffoldState,
                    onClickEditOrder = onClickEditOrder,
                    onClickViewOrder = onClickViewOrder,
                )
            },
            CartTabItem.DineInItem {
                DineInScreen(
                    navController = navController,
                    scaffoldState = scaffoldState,
                    onClickEditOrder = onClickEditOrder,
                    onClickViewOrder = onClickViewOrder,
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