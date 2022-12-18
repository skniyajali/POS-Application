package com.niyaj.popos.presentation.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.niyaj.popos.presentation.cart.dine_in.DineInScreen
import com.niyaj.popos.presentation.cart.dine_out.DineOutScreen
import com.niyaj.popos.presentation.components.StandardScaffold
import com.niyaj.popos.presentation.components.util.Tabs
import com.niyaj.popos.presentation.components.util.TabsContent
import com.niyaj.popos.presentation.util.Screen
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun CartScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
) {
    val pagerState = rememberPagerState()

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        navigationIcon = {},
        showBackArrow = true,
        navActions = {
            IconButton(
                onClick = {
                    navController.navigate(Screen.OrderScreen.route)
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
            CartTabItem.DineInItem {
                DineInScreen(
                    navController = navController,
                    bottomSheetScaffoldState = bottomSheetScaffoldState
                )
            },
            CartTabItem.DineOutItem {
                DineOutScreen(
                    navController = navController,
                    bottomSheetScaffoldState = bottomSheetScaffoldState
                )
            }
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